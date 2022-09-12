// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.
package main

import (
	"encoding/xml"

	"log"
	"os"
	"path/filepath"
	"sort"
	"strconv"
	"strings"
	"sync"
	"time"

	"github.com/HalCanary/booker/dom"
	"github.com/HalCanary/halcanary.github.io/check"
	"github.com/HalCanary/halcanary.github.io/filebuf"
)

type Blog struct {
	Title       string
	Description string
	Language    string // RFC-5646
	Copyright   string // "Copyright YEAR-YEAR Legal Entity. ALL RIGHTS RESERVED."
	BaseUrl     string // "https://example.com"
	Prefix      string // "/~example/"
	ImageLink   string // "/~example/image.png"
	Style       string // Inline CSS content.
	Icon        string // URL for icon/
	path        string // Infered to be os.Args[1] + "/docs" + .Prefix
}

var (
	waitgroup        sync.WaitGroup
	changedFiles     []string
	changedFilesChan chan string
)

func concat(strs ...string) string {
	return strings.Join(strs, "")
}

func (b Blog) Path() string { return b.path }

func ReadBlogConfig(blogRootPath string) (Blog, error) {
	var blog Blog
	configFile, err := os.Open(blogRootPath + "/Blog.xml")
	if err == nil {
		err = xml.NewDecoder(configFile).Decode(&blog)
		blog.path = concat(blogRootPath, "/docs", blog.Prefix)
	}
	return blog, err
}

func link(dst, text string) *dom.Node {
	return dom.Element("a", dom.Attr{"href": dst}, dom.TextNode(text))
}

// Syncronize access to the changedFiles data structure.
func readChangedFilesChan() {
	changedFilesChan = make(chan string)
	for s := range changedFilesChan {
		changedFiles = append(changedFiles, s)
	}
}

func main() {
	log.SetFlags(log.Lshortfile)

	if len(os.Args) < 2 {
		log.Fatal("usage?")
		return
	}

	blogRoot := os.Args[1]
	blog, err := ReadBlogConfig(blogRoot)
	check.Check(err)

	matches, err := filepath.Glob(blogRoot + "/src/BlogSrc/*.md")
	check.Check(err)

	var allPosts []Post
	for _, path := range matches {
		f, err := os.Open(path)
		check.Check(err)
		post, err := ParsePost(f)
		f.Close()
		check.Check(err)
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

	go readChangedFilesChan()

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
	node.RenderHTML(&f)
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

	ul := dom.Element("ul", dom.Attr{"class": "flat"}, dom.TextNode("\n"))
	main := dom.Element("div", dom.Attr{"role": "main"}, dom.TextNode("\n"))
	for _, p := range allPosts {
		ul.Append(
			dom.Elem("li", link("#"+p.LongId(), p.Title)),
			dom.TextNode("\n"),
		)
		main.Append(
			p.Article(2, concat(blog.BaseUrl, blog.Prefix, p.LongId(), "/"), blog.Prefix),
			dom.TextNode("\n"),
			dom.Elem("hr"),
			dom.TextNode("\n"),
		)
	}
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.TextNode("\n"),
		blog.makeHead(blog.Title),
		dom.TextNode("\n"),
		dom.Elem("body",
			dom.TextNode("\n"),
			dom.Elem("header",
				dom.TextNode("\n"),
				dom.Elem("h1", dom.TextNode(blog.Title)),
				dom.TextNode("\n"),
			),
			dom.TextNode("\n"),
			dom.Element("div", dom.Attr{"class": "centered", "role": "contentinfo"}, dom.TextNode(blog.Description)),
			dom.TextNode("\n"),

			searcher(blog.BaseUrl+blog.Prefix),
			dom.TextNode("\n"),
			dom.Elem("hr"),
			dom.TextNode("\n"),
			dom.Elem("nav",
				dom.TextNode("\n"),
				ul,
				dom.TextNode("\n"),
			),
			dom.TextNode("\n"),
			dom.Elem("hr"),
			dom.TextNode("\n"),
			main,
			dom.TextNode("\n"),
			dom.TextNode("\n"),
			lcr(
				prev,
				dom.Elem("p",
					dom.TextNode("("),
					link(blog.Prefix+"archives/", "ALL POSTS"),
					dom.TextNode(")"),
				),
				nil,
			),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
}

func (blog Blog) doSegmentedPosts(segmentedPostLists [][]Post, fragFn, titleFn func(p Post) string) {
	for idx, segment := range segmentedPostLists {
		check.Assert(len(segment) > 0)
		var prev, next *dom.Node
		if idx > 0 {
			p := segmentedPostLists[idx-1][0]
			next = dom.Elem("p", dom.TextNode("("), link(concat(blog.Prefix, fragFn(p)), titleFn(p)), dom.TextNode(")"))
		}
		if idx+1 < len(segmentedPostLists) {
			p := segmentedPostLists[idx+1][0]
			prev = dom.Elem("p", dom.TextNode("("), link(concat(blog.Prefix, fragFn(p)), titleFn(p)), dom.TextNode(")"))
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
	ul := dom.Elem("ul", dom.TextNode("\n"))
	var allcats []string
	for cat, _ := range categories {
		allcats = append(allcats, cat)
	}
	sort.Strings(allcats)
	for _, cat := range allcats {
		ul.Append(
			dom.Elem("li",
				link(
					concat(blog.Prefix, "category/", cat, "/"),
					concat("#", cat, " (", strconv.Itoa(categories[cat]), ")"),
				),
			),
			dom.TextNode("\n"),
		)
	}
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.TextNode("\n"),
		blog.makeHead(title),
		dom.TextNode("\n"),
		dom.Elem("body",
			dom.TextNode("\n"),
			dom.Elem("h1", dom.TextNode(title)),
			dom.TextNode("\n"),
			ul,
			dom.TextNode("\n"),
			dom.Elem("hr"),
			dom.TextNode("\n"),
			dom.Element("p", dom.Attr{"class": "centered"}, link("..", "BACK")),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
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
	div.Append(dom.TextNode("\n"))
	var byYear [][]Post = splitBy(posts, yearEq)
	for _, yearPosts := range byYear {
		if len(byYear) > 1 {
			div.Append(dom.Elem("h2", dom.TextNode(strconv.Itoa(yearPosts[0].Time.Year()))))
			div.Append(dom.TextNode("\n"))
		}
		ul := dom.Elem("ul")
		ul.Append(dom.TextNode("\n"))
		for _, post := range yearPosts {
			li := dom.Elem("li",
				dom.TextNode("\n  "),
				link(concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/"), post.Title),
				dom.TextNode("\n  "),
				dom.TextNode(post.Timestamp()),
			)
			if post.Summary != "" {
				li.Append(
					dom.TextNode("\n  "),
					dom.Elem("div", dom.TextNode(post.Summary)))
			}
			li.Append(dom.TextNode("\n"))
			ul.Append(li)
			ul.Append(dom.TextNode("\n"))
		}
		div.Append(ul)
		div.Append(dom.TextNode("\n"))
	}
	div.Append(dom.TextNode("\n"))
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.TextNode("\n"),
		head,
		dom.TextNode("\n"),
		dom.Elem("body",
			dom.TextNode("\n"),
			dom.Elem("h1", dom.TextNode(title)),
			dom.TextNode("\n"),
			div,
			dom.TextNode("\n"),
			dom.Elem("hr"),
			dom.TextNode("\n"),
			lcr(
				prev,
				dom.Elem("p", dom.TextNode("("), link("../", "UP"), dom.TextNode(")")),
				next,
			),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
}

func (blog Blog) makeHead(title string) *dom.Node {
	return dom.Elem("head",
		dom.TextNode("\n"),
		dom.Element("meta", dom.Attr{"charset": "utf-8"}),
		dom.TextNode("\n"),
		dom.Element("meta", dom.Attr{
			"name": "viewport", "content": "width=device-width, initial-scale=1.0"}),
		dom.TextNode("\n"),
		dom.Elem("title", dom.TextNode(title)),
		dom.TextNode("\n"),
		dom.Element("link", dom.Attr{"rel": "icon", "href": blog.Icon}),
		dom.TextNode("\n"),
		dom.Elem("style", dom.TextNode(blog.Style)),
		dom.TextNode("\n"),
		dom.Element("link", dom.Attr{
			"rel":   "alternate",
			"type":  "application/atom+xml",
			"title": blog.Title,
			"href":  concat(blog.BaseUrl, blog.Prefix, "rss.rss"),
		}),
		dom.TextNode("\n"),
		dom.Comment(concat("\n", blog.Copyright, "\n")),
		dom.TextNode("\n"),
	)
}

func (blog Blog) longLink(p *Post, description string) *dom.Node {
	if p == nil {
		return nil
	}
	return dom.Elem("p",
		dom.TextNode(concat("(", description, ": ")),
		link(concat(blog.BaseUrl, blog.Prefix, p.LongId(), "/"), p.Title),
		dom.TextNode(")"),
	)
}

func (blog Blog) makeIndividualPost(post Post, prev, next *Post) *dom.Node {
	return dom.Element("html", dom.Attr{"lang": blog.Language},
		dom.TextNode("\n"),
		blog.makeHead(concat(blog.Title, ": "+post.Title)),
		dom.TextNode("\n"),
		dom.Elem("body",
			dom.TextNode("\n"),
			post.Article(1, concat(blog.BaseUrl, blog.Prefix, post.LongId(), "/"), blog.Prefix),
			dom.TextNode("\n"),
			dom.Elem("hr"),
			dom.TextNode("\n"),
			lcr(
				blog.longLink(prev, "older"),
				dom.Elem("p",
					dom.TextNode("("),
					link(blog.Prefix+"archives/", "back"),
					dom.TextNode(")"),
				),
				blog.longLink(next, "newer"),
			),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
}

func replaceNilWithNbsp(v *dom.Node) *dom.Node {
	if v == nil {
		v = dom.RawHtml("&nbsp;") // return dom.TextNode("\u00a0")
	}
	return v
}

func lcr(prev, center, next *dom.Node) *dom.Node {
	return dom.Element("nav", dom.Attr{"aria-label": "External Navigation"},
		dom.TextNode("\n"),
		dom.Element("div", dom.Attr{"class": "lcr"},
			dom.TextNode("\n  "),
			dom.Elem("div", replaceNilWithNbsp(prev)),
			dom.TextNode("\n  "),
			dom.Element("div", dom.Attr{"class": "centered"}, replaceNilWithNbsp(center)),
			dom.TextNode("\n  "),
			dom.Element("div", dom.Attr{"class": "rightside"}, replaceNilWithNbsp(next)),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
}

func searcher(domain string) *dom.Node {
	return dom.Element("div", dom.Attr{"role": "search"},
		dom.Element("details", dom.Attr{"class": "rightside"},
			dom.TextNode("\n"),
			dom.Elem("summary", dom.TextNode("Search")),
			dom.TextNode("\n"),
			dom.Element("form", dom.Attr{"method": "get", "action": "https://www.google.com/search"},
				dom.TextNode("\n"),
				dom.Element("input", dom.Attr{"name": "domains", "value": domain, "type": "hidden"}),
				dom.TextNode("\n"),
				dom.Element("input", dom.Attr{"name": "sitesearch", "value": domain, "type": "hidden"}),
				dom.TextNode("\n"),
				dom.Element("input",
					dom.Attr{"id": "search", "name": "q", "size": "30", "maxlength": "255", "aria-labelledby": "submitter"}),
				dom.TextNode("\n"),
				dom.Element("input", dom.Attr{"id": "submitter", "value": "Search", "type": "submit"}),
				dom.TextNode("\n"),
			),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
}
