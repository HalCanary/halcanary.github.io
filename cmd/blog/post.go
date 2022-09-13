package main

import (
	"bufio"
	"bytes"
	"io"
	"math"
	"sort"
	"strconv"
	"strings"
	"time"

	"github.com/HalCanary/booker/dom"
	"github.com/HalCanary/halcanary.github.io/check"
	"github.com/HalCanary/halcanary.github.io/commonmarker"
	"gitlab.com/golang-commonmark/markdown"
	"golang.org/x/net/html"
	"golang.org/x/net/html/atom"
)

type Post struct {
	Source     string
	Title      string
	Author     string
	Time       time.Time
	TZone      string
	Id         string
	Summary    string
	Categories []string
	Markdown   []byte
}

func (p Post) LongId() string {
	return p.Time.Format("2006/01/02/") + p.Id
}

func (p Post) Datetime() string {
	return p.Time.Format("2006-01-02T15:04:05-07:00")
}

func (p Post) Timestamp() string {
	return p.Time.Format("2006-01-02 15:04:05-07:00 (MST)")
}

func header(level int, attributes dom.Attr, children ...*dom.Node) *dom.Node {
	return dom.Element("h"+strconv.Itoa(level), attributes, children...)
}

func parseHtml(s string) *dom.Node {
	div := &html.Node{Type: html.ElementNode, Data: "div", DataAtom:atom.Div}
	nodes, err := html.ParseFragment(strings.NewReader(s), div)
	check.Check(err)
	div.AppendChild(&html.Node{Type: html.TextNode, Data: "\n"})
	for _, n := range nodes {
		div.AppendChild(n)
	}
	div.AppendChild(&html.Node{Type: html.TextNode, Data: "\n"})
	return (*dom.Node)(div)
}

func (post Post) Content(level int) string {
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

func (post Post) Article(level int, url string, prefix string) *dom.Node {
	var cats *dom.Node
	if len(post.Categories) > 0 {
		cats = dom.Elem("div")
		for i, c := range post.Categories {
			if i != 0 {
				cats.Append(dom.TextNode("; "))
			}
			cats.Append(
				dom.Element("a", dom.Attr{
					"href":  concat(prefix, "category/", c, "/"),
					"class": "p-category",
				}, dom.TextNode("#"+c)),
			)
		}
	}

	var summary *dom.Node
	var summary2 *dom.Node
	if post.Summary != "" {
		summary = dom.Element("p", dom.Attr{"class": "p-summary"}, dom.TextNode(post.Summary))
		summary2 = dom.Element("div", dom.Attr{"style": "display:none;"}, dom.TextNode(post.Summary))
	}
	return dom.Element("article", dom.Attr{"id": post.LongId(), "class": "h-entry"},
		dom.TextNode("\n"),
		dom.Elem("header",
			dom.TextNode("\n"),
			dom.Comment(concat(" SRC= ", post.Source, " ")),
			dom.TextNode("\n"),
			header(level, dom.Attr{"class": "blogtitle p-name"}, dom.TextNode(post.Title)),
			dom.TextNode("\n"),
			summary,
			dom.TextNode("\n"),
			dom.Element("div", dom.Attr{"class": "byline plainlink"},
				dom.TextNode("\n"),
				dom.Elem("div",
					dom.TextNode("\n"),
					dom.Element("div", dom.Attr{"class": "p-author"}, dom.TextNode(post.Author)),
					dom.TextNode("\n"),
					dom.Elem("div",
						dom.Element("time",
							dom.Attr{"datetime": post.Datetime(), "class": "dt-published"},
							dom.TextNode(post.Timestamp()),
						),
					),
					dom.TextNode("\n"),
					dom.Elem("div",
						dom.Element("a", dom.Attr{"href": url, "class": "u-url u-uid"}, dom.TextNode(url)),
					),
					dom.TextNode("\n"),
					cats,
					dom.TextNode("\n"),
				),
				dom.TextNode("\n"),
			),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
		dom.Element("div", dom.Attr{"class": "content e-content"},
			dom.TextNode("\n"),
			summary2,
			dom.TextNode("\n"),
			parseHtml(post.Content(level)),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
}

func ParsePost(src io.Reader) (post Post, err error) {
	scanner := bufio.NewScanner(src)
	for scanner.Scan() {
		splt := strings.SplitN(scanner.Text(), "=", 2)
		if len(splt) != 2 {
			break
		}
		value := strings.TrimSpace(splt[1])
		switch strings.TrimSpace(splt[0]) {
		case "TITLE":
			post.Title = value
		case "DATE":
			post.Time, err = time.Parse("2006-01-02 15:04:05Z07:00 (MST)", value)
			if err != nil {
				return
			}
		case "AUTHOR":
			post.Author = value
		case "POSTID":
			post.Id = value
		case "SUMMARY":
			post.Summary = value
		case "CATEGORIES":
			cats := strings.Split(value, ";")
			for _, c := range cats {
				if c != "" {
					post.Categories = append(post.Categories, c)
				}
			}
		}
	}
	var buffer bytes.Buffer
	for scanner.Scan() {
		buffer.Write(scanner.Bytes())
		buffer.Write([]byte{'\n'})
	}
	post.Markdown = buffer.Bytes()
	err = scanner.Err()
	return
}

type postList []Post

func (a postList) Len() int           { return len(a) }
func (a postList) Swap(i, j int)      { a[i], a[j] = a[j], a[i] }
func (a postList) Less(i, j int) bool { return a[j].Time.Before(a[i].Time) }

func SortPosts(posts []Post) {
	sort.Sort(postList(posts))
}
