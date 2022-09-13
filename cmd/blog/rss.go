package main

// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.

import (
	"crypto/sha256"
	"encoding/xml"
	"html"
	"io"
	"strings"
	"time"

	"github.com/google/uuid"
)

type xmlElem struct {
	XMLName  xml.Name
	Attrs    []xml.Attr `xml:",any,attr"`
	Children []xmlElem  `xml:",any"`
	Data     string     `xml:",chardata"`
	CData    string     `xml:",cdata"`
	IData    string     `xml:",innerxml"`
}

func tag(k, v string, a ...xml.Attr) xmlElem {
	return xmlElem{XMLName: xml.Name{Local: k}, Data: v, Attrs: a}
}

func MakeRSS(now time.Time, blog Blog, posts []Post, dst io.Writer) error {
	blogLink := concat(blog.BaseUrl, blog.Prefix)
	items := []xmlElem{
		tag("atom:link", "",
			xml.Attr{Name: xml.Name{Local: "href"}, Value: concat(blog.BaseUrl, blog.Prefix, "rss.rss")},
			xml.Attr{Name: xml.Name{Local: "rel"}, Value: "self"},
			xml.Attr{Name: xml.Name{Local: "type"}, Value: "application/rss+xml"},
		),
		tag("title", blog.Title),
		tag("description", blog.Description),
		tag("language", blog.Language),
		tag("copyright", blog.Copyright),
		tag("link", blogLink),
		tag("pubDate", now.Format(time.RFC1123Z)),
		tag("lastBuildDate", now.Format(time.RFC1123Z)),
		xmlElem{
			XMLName: xml.Name{Local: "image"},
			Children: []xmlElem{
				tag("url", blog.ImageLink),
				tag("title", blog.Title),
				tag("link", blogLink),
			},
		},
	}
	for _, p := range posts {
		url := concat(blog.BaseUrl, blog.Prefix, p.LongId(), "/")
		content := PostContent(p, 1)
		if p.Summary != "" {
			content = concat("<p>", html.EscapeString(p.Summary), "</p>\n", content)
		}
		tags := []xmlElem{
			xmlElem{XMLName: xml.Name{Local: "title"}, IData: hexEscapeString(p.Title)},
			tag("link", url),
			tag("guid", uuid.NewHash(sha256.New(), uuid.UUID{}, []byte(url+content), 5).String(),
				xml.Attr{Name: xml.Name{Local: "isPermaLink"}, Value: "false"}),
			tag("pubDate", p.Time.Format(time.RFC1123Z)),
			xmlElem{XMLName: xml.Name{Local: "description"}, CData: content},
		}
		if len(p.Categories) > 0 {
			tags = append(tags, tag("category", strings.Join(p.Categories, ",")))
		}
		items = append(items, xmlElem{XMLName: xml.Name{Local: "item"}, Children: tags})
	}
	rss := xmlElem{
		XMLName: xml.Name{Local: "rss"},
		Attrs: []xml.Attr{
			xml.Attr{Name: xml.Name{Local: "version"}, Value: "2.0"},
			xml.Attr{Name: xml.Name{Local: "xmlns:atom"}, Value: "http://www.w3.org/2005/Atom"},
		},
		Children: []xmlElem{
			xmlElem{
				XMLName:  xml.Name{Local: "channel"},
				Children: items,
			},
		},
	}
	dst.Write([]byte(xml.Header))
	enc := xml.NewEncoder(dst)
	enc.Indent("", "\t")
	return enc.Encode(&rss)
}

var (
	escTab  = []byte("&#x9;")
	escNL   = []byte("&#xA;")
	escCR   = []byte("&#xD;")
	escQuot = []byte("&#x22;")
	escApos = []byte("&#x27;")
	escAmp  = []byte("&#x26;")
	escLT   = []byte("&#x3C;")
	escGT   = []byte("&#x3E;")
)

func hexEscapeString(s string) string {
	var buffer strings.Builder
	buffer.Grow(len(s))
	for _, r := range s {
		var esc []byte
		switch r {
		case '"':
			esc = escQuot
		case '\'':
			esc = escApos
		case '&':
			esc = escAmp
		case '<':
			esc = escLT
		case '>':
			esc = escGT
		case '\t':
			esc = escTab
		case '\n':
			esc = escNL
		case '\r':
			esc = escCR
		default:
			buffer.WriteRune(r)
		}
		if esc != nil {
			buffer.Write(esc)
		}
	}
	return buffer.String()
}
