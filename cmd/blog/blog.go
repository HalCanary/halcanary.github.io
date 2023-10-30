// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.
package main

import (
	"bytes"
	"encoding/xml"
	"flag"
	"log"
	"math"
	"os"
	"path/filepath"
	"regexp"
	"sort"
	"strconv"
	"strings"
	"sync"
	"time"
	"unicode/utf8"

	"github.com/HalCanary/facility/dom"
	"github.com/HalCanary/halcanary.github.io/check"
	"github.com/HalCanary/halcanary.github.io/commonmarker"
	"github.com/HalCanary/halcanary.github.io/filebuf"
	"github.com/HalCanary/halcanary.github.io/logpost"
	"gitlab.com/golang-commonmark/markdown"
	"golang.org/x/net/html"
	"golang.org/x/net/html/atom"
	"golang.org/x/text/unicode/norm"
)

type Blog struct {
	Title       string
	Description string
	Language    string // RFC-5646
	Copyright   string // "Copyright YEAR-YEAR Legal Entity."
	License     string // "ALL RIGHTS RESERVED."
	BaseUrl     string // "https://example.com"
	Prefix      string // "/~example/"
	ImageLink   string // "/~example/image.png"
	Style       string // Inline CSS content.
	Icon        string // URL for icon/
	StatusBytes int    // Maximum length for status mesages.
	path        string // Infered to be os.Args[1] + "/docs"
}

const timestampFormat = "2006-01-02 15:04:05Z07:00 (MST)"

var urlRe = regexp.MustCompile("https?://[-#%&+,./:=?@_~A-Za-z0-9]*")

var (
	waitgroup        sync.WaitGroup
	changedFiles     []string
	changedFilesChan chan string
)

func concat(strs ...string) string {
	return strings.Join(strs, "")
}

func (b Blog) Path() string { return concat(b.path, b.Prefix) }

func ReadBlogConfig(blogRootPath string) (Blog, error) {
	var blog Blog
	configFile, err := os.Open(blogRootPath + "/Blog.xml")
	if err == nil {
		err = xml.NewDecoder(configFile).Decode(&blog)
		blog.path = concat(blogRootPath, "/docs")
	}
	return blog, err
}

func link(dst, text string) *dom.Node {
	return dom.Element("a", dom.Attr{"href": dst}, dom.Text(text))
}

// Syncronize access to the changedFiles data structure.
func readChangedFilesChan() {
	changedFilesChan = make(chan string)
	for s := range changedFilesChan {
		changedFiles = append(changedFiles, s)
	}
}

type Post = logpost.Post
type postList []Post

func (a postList) Len() int           { return len(a) }
func (a postList) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a postList) Less(i, j int) bool { return a[j].Time.Before(a[i].Time) }

func SortPosts(posts []Post) {
	sort.Sort(postList(posts))
}

func postUrl(blog Blog, post logpost.Post) string {
	return concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/")
}

func status(path string, blog Blog, p logpost.Post) {
	b := filebuf.FileBuf{Path: path}
	var top bytes.Buffer
	top.WriteString("Blog post:\n")
	top.WriteString(p.Title)
	top.WriteString("\n")
	if p.Summary != "" {
		top.WriteString(p.Summary)
		top.WriteString("\n")
	}
	top.WriteString("\n")
	var bottom bytes.Buffer
	bottom.WriteString("\n\nRead more: ")
	bottom.WriteString(postUrl(blog, p))
	if len(p.Categories) > 0 {
		bottom.WriteString("\n\n#")
		bottom.WriteString(p.Categories[0])
		for _, c := range p.Categories[1:] {
			bottom.WriteString(" | #")
			bottom.WriteString(c)
		}
	}
	bottom.WriteString("\n")
	bottomBytes := bottom.Bytes()
	topBytes := top.Bytes()

	var maximumStatusLength = 500
	if blog.StatusBytes > 0 {
		maximumStatusLength = blog.StatusBytes
	}
	remaining := maximumStatusLength - countMastodonBytes(topBytes) - countMastodonBytes(bottomBytes)
	const ellipsis = "…"
	var lenEllipsis = utf8.RuneCountInString(ellipsis)
	b.Write(topBytes)
	if countMastodonBytes(p.Markdown) < remaining {
		b.Write(p.Markdown)
	} else if remaining > lenEllipsis {
		b.Write(trimMastodonCount(p.Markdown, remaining-lenEllipsis))
		b.WriteString(ellipsis)
	}
	b.Write(bottomBytes)
	check.Check(b.Close())
	if b.Changed() {
		changedFilesChan <- b.Path
	}
}

func countMastodonBytes(s []byte) int {
	size := utf8.RuneCount(s)
	var match []byte
	for _, match = range urlRe.FindAll(s, -1) {
		size += 23 - utf8.RuneCount(match)
	}
	return size
}

func trimMastodonCount(source []byte, maxCount int) []byte {
	var match []int
	var last int = 0
	for _, match = range urlRe.FindAllIndex(source, -1) {
		if len(match) == 2 {
			s := source[last:match[0]]
			if utf8.RuneCount(s) <= maxCount {
				last = match[0]
				maxCount -= utf8.RuneCount(s)
			} else {
				for maxCount > 0 && len(s) > 0 {
					_, byteCount := utf8.DecodeRune(s)
					last += byteCount
					s = s[byteCount:]
					maxCount -= 1
				}
				return source[:last]
			}
			if maxCount < 23 {
				return source[:last]
			}
			last = match[1]
			maxCount -= 23
		}
	}
	s := source[last:]
	if utf8.RuneCount(s) <= maxCount {
		return source
	} else {
		for maxCount > 0 && len(s) > 0 {
			_, byteCount := utf8.DecodeRune(s)
			last += byteCount
			s = s[byteCount:]
			maxCount -= 1
		}
	}
	return source[:last]
}

func main() {
	log.SetFlags(log.Lshortfile)
	var onlyPrintCats bool
	flag.BoolVar(&onlyPrintCats, "cats", false, "Only print categories.")
	flag.Parse()

	if len(flag.Args()) < 1 {
		log.Fatal("usage?")
	}

	blogRoot := flag.Arg(0)
	blog, err := ReadBlogConfig(blogRoot)
	check.Check(err)
	matches, err := filepath.Glob(blogRoot + "/src/BlogSrc/*.md")
	check.Check(err)

	var allPosts []logpost.Post
	for _, path := range matches {
		content, err := os.ReadFile(path)
		check.Check(err)
		content = norm.NFC.Bytes(content)
		post, err := logpost.Parse(bytes.NewReader(content))
		check.Check(err)
		if len(post.Markdown) == 0 && post.Title == "" && post.Summary == "" {
			log.Printf("%q has no content.\n", path)
			continue
		}
		check.Assert(post.Id != "")
		if post.Title == "" {
			post.Title = post.Time.Format("January 2, 2006")
		}
		post.Source = filepath.Base(path)
		allPosts = append(allPosts, post)
	}
	SortPosts(allPosts)

	var lastMod time.Time
	for _, path := range matches {
		info, err := os.Stat(path)
		check.Check(err)
		mt := info.ModTime()
		if lastMod.IsZero() || lastMod.Before(mt) {
			lastMod = mt
		}

	}
	if onlyPrintCats {
		cats := map[string]struct{}{}
		for _, post := range allPosts {
			for _, c := range post.Categories {
				cats[c] = struct{}{}
			}
		}
		var allCats []string
		for c, _ := range cats {
			allCats = append(allCats, c)
		}
		sort.Strings(allCats)
		for _, c := range allCats {
			os.Stdout.WriteString("\t")
			os.Stdout.WriteString(c)
			os.Stdout.WriteString("\n")
		}
		return
	}

	go readChangedFilesChan()

	for _, p := range allPosts {
		path := concat(blogRoot, "/status/", p.Time.Format("2006-01-02"), "-", p.Id, ".txt")
		status(path, blog, p)
	}

	for idx, post := range allPosts {
		var prev, next *Post
		if idx > 0 {
			next = &allPosts[idx-1]
		}
		if idx+1 < len(allPosts) {
			prev = &allPosts[idx+1]
		}

		waitgroup.Add(1)
		go writeIndividualPost(blog, post, prev, next)
	}

	waitgroup.Add(1)
	go writeSegmentedListingPage(blog, allPosts, "archives/", blog.Title+" Archives", nil, nil)
	writeListingPage(blog, allPosts)

	blog.doSegmentedPosts(splitBy(allPosts, yearEq), yearFrag, yearTitle)
	blog.doSegmentedPosts(splitBy(allPosts, monthEq), monthFrag, monthTitle)
	blog.doSegmentedPosts(splitBy(allPosts, dayEq), dayFrag, dayTitle)

	blog.doCategories(allPosts)

	waitgroup.Add(1)
	go writeIndex(blog, allPosts)

	waitgroup.Add(1)
	go writeRSS(blog, allPosts, lastMod)

	waitgroup.Wait()
	if len(changedFiles) == 0 {
		log.Printf("nothing changed.")
	} else if len(changedFiles) > 100 {
		log.Printf("%d changed files.\n", len(changedFiles))
	} else {
		log.Printf("%d changed files:\n%s\n", len(changedFiles), strings.Join(changedFiles, "\n"))
	}
}

func writeIndividualPost(blog Blog, post Post, prev, next *Post) {
	updateHtml(
		concat(blog.Path(), post.LongId(), "/index.html"),
		blog.makeIndividualPost(post, prev, next))
	waitgroup.Done()
}

func writeListingPage(blog Blog, posts []Post) {
	if len(posts) == 0 {
		return
	}
	head := blog.makeHead(blog.Title)
	div := dom.Element("div", dom.Attr{"role": "main"})
	dom.Append(div, dom.Text("\n"))
	var byYear [][]Post = splitBy(posts, yearEq)
	for _, yearPosts := range byYear {
		yearString := strconv.Itoa(yearPosts[0].Time.Year())
		year := dom.Element("div", dom.Attr{"id": yearString})
		if len(byYear) > 1 {
			dom.Append(year, dom.Elem("h2", dom.Text(yearString)))
			dom.Append(year, dom.Text("\n"))
		}
		dom.Append(year, dom.Text("\n"))
		for _, post := range yearPosts {
			summary := dom.Elem("summary",
				dom.Text("\n  "),
				link(concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/"), post.Title),
				dom.Text("\n  "),
				dom.Text(post.Time.Format(timestampFormat)),
			)
			if post.Summary != "" {
				dom.Append(summary,
					dom.Text("\n  "),
					dom.Elem("br"),
					dom.Text(post.Summary))
			}
			article := PostArticle(post, 3, concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/"), blog.Prefix)
			addImageSize(article, blog.BaseUrl, blog.path)
			details := dom.Element("details", dom.Attr{"id": "dt/" + post.LongId()},
				summary, dom.Text("\n"),
				dom.Element("div", dom.Attr{"class": "box"}, article),
				dom.Text("\n"))
			dom.Append(year, details, dom.Text("\n"))
		}
		dom.Append(year, dom.Text("\n"))
		dom.Append(div, year, dom.Text("\n"))
	}
	dom.Append(div, dom.Text("\n"))
	html := dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.Text("\n"),
		head,
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			dom.Elem("h1", dom.Text(blog.Title)),
			dom.Text("\n"),
			div,
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
			lcr(
				dom.Text(""),
				dom.Elem("p", dom.Text("("), link("../", "UP"), dom.Text(")")),
				dom.Text(""),
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
	updateHtml(concat(blog.Path(), "all", "/index.html"), html)
}

func writeSegmentedListingPage(blog Blog, segment []Post, fragment, title string, prev, next *dom.Node) {
	updateHtml(
		concat(blog.Path(), fragment, "index.html"),
		blog.makeListingPage(segment, title, prev, next))
	waitgroup.Done()
}

func writeIndex(blog Blog, allPosts []Post) {
	updateHtml(
		concat(blog.Path(), "index.html"),
		blog.makeIndex(allPosts))
	waitgroup.Done()
}

func writeRSS(blog Blog, allPosts []Post, lastMod time.Time) {
	rss := filebuf.FileBuf{Path: concat(blog.Path(), "rss.rss")}
	check.Check(MakeRSS(lastMod, blog, allPosts, &rss))
	check.Check(rss.Close())
	if rss.Changed() {
		changedFilesChan <- rss.Path
	}
	waitgroup.Done()
}

func writeCategoryIndex(blog Blog, categories map[string]int) {
	updateHtml(
		concat(blog.Path(), "category/index.html"),
		blog.makeCategoryIndex(categories),
	)
	waitgroup.Done()
}

func updateHtml(path string, node *dom.Node) {
	f := filebuf.FileBuf{Path: path}
	dom.RenderHTMLExperimental(node, &f)
	check.Check(f.Close())
	if f.Changed() {
		changedFilesChan <- f.Path
	}
}

func yearTitle(p Post) string  { return p.Time.Format("2006") }
func monthTitle(p Post) string { return p.Time.Format("2006-01") }
func dayTitle(p Post) string   { return p.Time.Format("2006-01-02") }

func yearFrag(p Post) string  { return p.Time.Format("2006/") }
func monthFrag(p Post) string { return p.Time.Format("2006/01/") }
func dayFrag(p Post) string   { return p.Time.Format("2006/01/02/") }

func yearEq(u, v Post) bool { return u.Time.Year() == v.Time.Year() }

func monthEq(u, v Post) bool {
	yu, mu, _ := u.Time.Date()
	yv, mv, _ := v.Time.Date()
	return yu == yv && mu == mv
}

func dayEq(u, v Post) bool {
	yu, mu, du := u.Time.Date()
	yv, mv, dv := v.Time.Date()
	return yu == yv && mu == mv && du == dv
}

func (blog Blog) makeIndex(allPosts []Post) *dom.Node {
	const count = 10
	var prev *dom.Node
	if len(allPosts) > count {
		prev = blog.longLink(&allPosts[count], "older")
		allPosts = allPosts[:count]
	}

	ul := dom.Element("ul", dom.Attr{"class": "flat"}, dom.Text("\n"))
	main := dom.Element("div", dom.Attr{"role": "main"}, dom.Text("\n"))
	for _, p := range allPosts {
		dom.Append(ul,
			dom.Elem("li", link("#"+p.LongId(), p.Title)),
			dom.Text("\n"),
		)
		article := PostArticle(p, 2, concat(blog.BaseUrl, blog.Prefix, p.LongId(), "/"), blog.Prefix)
		addImageSize(article, blog.BaseUrl, blog.path)
		dom.Append(main,
			article,
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
		)
	}
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.Text("\n"),
		blog.makeHead(blog.Title),
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			dom.Elem("header",
				dom.Text("\n"),
				dom.Elem("h1", dom.Text(blog.Title)),
				dom.Text("\n"),
			),
			dom.Text("\n"),
			dom.Element("div", dom.Attr{"class": "centered", "role": "contentinfo"}, dom.Text(blog.Description)),
			dom.Text("\n"),

			searcher(blog.BaseUrl+blog.Prefix),
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
			dom.Elem("nav",
				dom.Text("\n"),
				ul,
				dom.Text("\n"),
			),
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
			main,
			dom.Text("\n"),
			dom.Text("\n"),
			lcr(
				prev,
				dom.Elem("p",
					dom.Text("("),
					link(blog.Prefix+"archives/", "ALL POSTS"),
					dom.Text(")"),
				),
				nil,
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}

func (blog Blog) doSegmentedPosts(segmentedPostLists [][]Post, fragFn, titleFn func(p Post) string) {
	for idx, segment := range segmentedPostLists {
		check.Assert(len(segment) > 0)
		var prev, next *dom.Node
		if idx > 0 {
			p := segmentedPostLists[idx-1][0]
			next = dom.Elem("p", dom.Text("("), link(concat(blog.Prefix, fragFn(p)), titleFn(p)), dom.Text(")"))
		}
		if idx+1 < len(segmentedPostLists) {
			p := segmentedPostLists[idx+1][0]
			prev = dom.Elem("p", dom.Text("("), link(concat(blog.Prefix, fragFn(p)), titleFn(p)), dom.Text(")"))
		}
		fragment := fragFn(segment[0])
		title := concat(blog.Title, " Archive ", titleFn(segment[0]))
		waitgroup.Add(1)
		go writeSegmentedListingPage(blog, segment, fragment, title, prev, next)
	}
}

func splitBy(posts []Post, equivilent func(Post, Post) bool) [][]Post {
	// assume posts already sorted with equivilent posts together.
	var sorted [][]Post
	if len(posts) > 0 {
		var currentList []Post
		for _, post := range posts {
			if len(currentList) > 0 && !equivilent(post, currentList[0]) {
				sorted = append(sorted, currentList)
				currentList = nil
			}
			currentList = append(currentList, post)
		}
		sorted = append(sorted, currentList)
	}
	return sorted
}

func (blog Blog) doCategories(allPosts []Post) {
	catmap := map[string][]Post{}
	for _, p := range allPosts {
		for _, cat := range p.Categories {
			list, _ := catmap[cat]
			catmap[cat] = append(list, p)
		}
		if len(p.Categories) == 0 {
			cat := "uncategorized"
			list, _ := catmap[cat]
			catmap[cat] = append(list, p)
		}
	}
	categories := map[string]int{}
	for cat, postList := range catmap {
		categories[cat] = len(postList)
	}
	waitgroup.Add(1)
	go writeCategoryIndex(blog, categories)
	for cat, postList := range catmap {
		waitgroup.Add(1)
		fragment := concat("category/", cat, "/")
		title := concat(blog.Title, " Archive #", cat)
		go writeSegmentedListingPage(blog, postList, fragment, title, nil, nil)
	}
}

func (blog Blog) makeCategoryIndex(categories map[string]int) *dom.Node {
	title := blog.Title + " Archive by Category"
	ul := dom.Elem("ul", dom.Text("\n"))
	var allcats []string
	for cat, _ := range categories {
		allcats = append(allcats, cat)
	}
	sort.Strings(allcats)
	for _, cat := range allcats {
		dom.Append(ul,
			dom.Elem("li",
				link(
					concat(blog.Prefix, "category/", cat, "/"),
					concat("#", cat, " (", strconv.Itoa(categories[cat]), ")"),
				),
			),
			dom.Text("\n"),
		)
	}
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.Text("\n"),
		blog.makeHead(title),
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			dom.Elem("h1", dom.Text(title)),
			dom.Text("\n"),
			ul,
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
			dom.Element("p", dom.Attr{"class": "centered"}, link("..", "BACK")),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}

func (blog Blog) makeListingPage(posts []Post, title string, prev, next *dom.Node) *dom.Node {
	if prev == nil {
		prev = dom.RawHtml("&nbsp;")
	}
	if next == nil {
		next = dom.RawHtml("&nbsp;")
	}

	if len(posts) == 0 {
		return nil
	}
	head := blog.makeHead(title)

	div := dom.Element("div", dom.Attr{"role": "main"})
	dom.Append(div, dom.Text("\n"))
	var byYear [][]Post = splitBy(posts, yearEq)
	for _, yearPosts := range byYear {
		if len(byYear) > 1 {
			dom.Append(div, dom.Elem("h2", dom.Text(strconv.Itoa(yearPosts[0].Time.Year()))))
			dom.Append(div, dom.Text("\n"))
		}
		ul := dom.Elem("ul")
		dom.Append(ul, dom.Text("\n"))
		for _, post := range yearPosts {
			li := dom.Elem("li",
				dom.Text("\n  "),
				link(concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/"), post.Title),
				dom.Text("\n  "),
				dom.Text(post.Time.Format(timestampFormat)),
			)
			if post.Summary != "" {
				dom.Append(li,
					dom.Text("\n  "),
					dom.Elem("div", dom.Text(post.Summary)))
			}
			dom.Append(li, dom.Text("\n"))
			dom.Append(ul, li)
			dom.Append(ul, dom.Text("\n"))
		}
		dom.Append(div, ul)
		dom.Append(div, dom.Text("\n"))
	}
	dom.Append(div, dom.Text("\n"))
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.Text("\n"),
		head,
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			dom.Elem("h1", dom.Text(title)),
			dom.Text("\n"),
			div,
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
			lcr(
				prev,
				dom.Elem("p", dom.Text("("), link("../", "UP"), dom.Text(")")),
				next,
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}

func (blog Blog) makeHead(title string) *dom.Node {
	return dom.Elem("head",
		dom.Text("\n"),
		dom.Element("meta", dom.Attr{"charset": "utf-8"}),
		dom.Text("\n"),
		dom.Element("meta", dom.Attr{
			"name": "viewport", "content": "width=device-width, initial-scale=1.0"}),
		dom.Text("\n"),
		dom.Elem("title", dom.Text(title)),
		dom.Text("\n"),
		dom.Element("link", dom.Attr{"rel": "icon", "href": blog.Icon}),
		dom.Text("\n"),
		dom.Elem("style", dom.Text(blog.Style)),
		dom.Text("\n"),
		dom.Element("link", dom.Attr{
			"rel":   "alternate",
			"type":  "application/atom+xml",
			"title": blog.Title,
			"href":  concat(blog.BaseUrl, blog.Prefix, "rss.rss"),
		}),
		dom.Text("\n"),
		dom.Comment(concat("\n", blog.Copyright, " ", blog.License, "\n")),
		dom.Text("\n"),
	)
}

func (blog Blog) longLink(p *Post, description string) *dom.Node {
	if p == nil {
		return nil
	}
	return dom.Elem("p",
		dom.Text(concat("(", description, ": ")),
		link(concat(blog.BaseUrl, blog.Prefix, p.LongId(), "/"), p.Title),
		dom.Text(")"),
	)
}

func (blog Blog) makeIndividualPost(post Post, prev, next *Post) *dom.Node {
	article := PostArticle(post, 1, concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/"), blog.Prefix)
	addImageSize(article, blog.BaseUrl, blog.path)
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.Text("\n"),
		blog.makeHead(concat(blog.Title, ": "+post.Title)),
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			article,
			dom.Text("\n"),
			dom.Elem("hr"),
			dom.Text("\n"),
			lcr(
				blog.longLink(prev, "older"),
				dom.Elem("p",
					dom.Text("("),
					link(blog.Prefix+"archives/", "back"),
					dom.Text(")"),
				),
				blog.longLink(next, "newer"),
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}

func replaceNilWithNbsp(v *dom.Node) *dom.Node {
	if v == nil {
		v = dom.RawHtml("&nbsp;") // return dom.Text("\u00a0")
	}
	return v
}

func lcr(prev, center, next *dom.Node) *dom.Node {
	return dom.Element("nav", dom.Attr{"aria-label": "External Navigation"},
		dom.Text("\n"),
		dom.Element("div", dom.Attr{"class": "lcr"},
			dom.Text("\n  "),
			dom.Elem("div", replaceNilWithNbsp(prev)),
			dom.Text("\n  "),
			dom.Element("div", dom.Attr{"class": "centered"}, replaceNilWithNbsp(center)),
			dom.Text("\n  "),
			dom.Element("div", dom.Attr{"class": "rightside"}, replaceNilWithNbsp(next)),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}

func searcher(domain string) *dom.Node {
	return dom.Element("div", dom.Attr{"role": "search"},
		dom.Element("details", dom.Attr{"class": "rightside"},
			dom.Text("\n"),
			dom.Elem("summary", dom.Text("Search")),
			dom.Text("\n"),
			dom.Element("form", dom.Attr{"method": "get", "action": "https://www.google.com/search"},
				dom.Text("\n"),
				dom.Element("input", dom.Attr{"name": "domains", "value": domain, "type": "hidden"}),
				dom.Text("\n"),
				dom.Element("input", dom.Attr{"name": "sitesearch", "value": domain, "type": "hidden"}),
				dom.Text("\n"),
				dom.Element("input",
					dom.Attr{"id": "search", "name": "q", "size": "30", "maxlength": "255", "aria-labelledby": "submitter"}),
				dom.Text("\n"),
				dom.Element("input", dom.Attr{"id": "submitter", "value": "Search", "type": "submit"}),
				dom.Text("\n"),
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}

func header(level int, attributes dom.Attr, children ...*dom.Node) *dom.Node {
	return dom.Element("h"+strconv.Itoa(level), attributes, children...)
}

func parseHtml(s string) *dom.Node {
	div := &html.Node{Type: html.ElementNode, Data: "div", DataAtom: atom.Div}
	nodes, err := html.ParseFragment(strings.NewReader(s), div)
	check.Check(err)
	div.AppendChild(&html.Node{Type: html.TextNode, Data: "\n"})
	for _, n := range nodes {
		div.AppendChild(n)
	}
	div.AppendChild(&html.Node{Type: html.TextNode, Data: "\n"})
	return (*dom.Node)(div)
}

func PostContent(post Post, level int) string {
	markdowner := commonmarker.Markdowner()
	tokens := markdowner.Parse(post.Markdown)
	lowestLevel := math.MaxInt
	for _, token := range tokens {
		if h, ok := token.(*markdown.HeadingOpen); ok && h != nil {
			if h.HLevel < lowestLevel {
				lowestLevel = h.HLevel
			}
		}
	}
	if lowestLevel < math.MaxInt {
		levelChange := level + 1 - lowestLevel
		for _, token := range tokens {
			if h, ok := token.(*markdown.HeadingOpen); ok && h != nil {
				h.HLevel += levelChange
			} else if h, ok := token.(*markdown.HeadingClose); ok && h != nil {
				h.HLevel += levelChange
			}
		}
	}
	return markdowner.RenderTokensToString(tokens)
}

func PostArticle(post Post, level int, url string, prefix string) *dom.Node {
	var cats *dom.Node
	if len(post.Categories) > 0 {
		cats = dom.Elem("div")
		for i, c := range post.Categories {
			if i != 0 {
				dom.Append(cats, dom.Text("; "))
			}
			dom.Append(cats,
				dom.Element("a", dom.Attr{
					"href":  concat(prefix, "category/", c, "/"),
					"class": "p-category",
				}, dom.Text("#"+c)),
			)
		}
	}

	var summary *dom.Node
	var summary2 *dom.Node
	if post.Summary != "" {
		summary = dom.Element("p", dom.Attr{"class": "p-summary"}, dom.Text(post.Summary))
		summary2 = dom.Element("div", dom.Attr{"style": "display:none;"}, dom.Text(post.Summary))
	}
	return dom.Element("article", dom.Attr{"id": post.LongId(), "class": "h-entry"},
		dom.Text("\n"),
		dom.Elem("header",
			dom.Text("\n"),
			dom.Comment(concat(" SRC= ", post.Source, " ")),
			dom.Text("\n"),
			header(level, dom.Attr{"class": "blogtitle p-name"}, dom.Text(post.Title)),
			dom.Text("\n"),
			summary,
			dom.Text("\n"),
			dom.Element("div", dom.Attr{"class": "byline plainlink"},
				dom.Text("\n"),
				dom.Elem("div",
					dom.Text("\n"),
					dom.Element("div", dom.Attr{"class": "p-author"}, dom.Text(post.Author)),
					dom.Text("\n"),
					dom.Elem("div",
						dom.Element("time",
							dom.Attr{"datetime": post.Time.Format(time.RFC3339), "class": "dt-published"},
							dom.Text(post.Time.Format(timestampFormat)),
						),
					),
					dom.Text("\n"),
					dom.Elem("div",
						dom.Element("a", dom.Attr{"href": url, "class": "u-url u-uid"}, dom.Text(url)),
					),
					dom.Text("\n"),
					cats,
					dom.Text("\n"),
				),
				dom.Text("\n"),
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
		dom.Element("div", dom.Attr{"class": "content e-content"},
			dom.Text("\n"),
			summary2,
			dom.Text("\n"),
			parseHtml(PostContent(post, level)),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
}
