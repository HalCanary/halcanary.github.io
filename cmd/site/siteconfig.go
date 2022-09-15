package main

// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.

import (
	"encoding/xml"
	"os"

	"github.com/HalCanary/booker/dom"
)

type SiteConfig struct {
	Language  string // RFC-5646
	Copyright string // "Copyright YEAR-YEAR Legal Entity."
	License   string // "ALL RIGHTS RESERVED."
	BaseUrl   string // "https://example.com"
	Style     string // Inline CSS content.
	Icon      string // URL for icon/
	Banner    string
}

func ReadSiteConfig(siteRootPath string) (SiteConfig, error) {
	var c SiteConfig
	configFile, err := os.Open(siteRootPath + "/Blog.xml")
	if err == nil {
		err = xml.NewDecoder(configFile).Decode(&c)
	}
	return c, err
}

func (siteConfig SiteConfig) MakeHead(title string) *dom.Node {
	return dom.Elem("head",
		dom.TextNode("\n"),
		dom.Element("meta", dom.Attr{"charset": "utf-8"}),
		dom.TextNode("\n"),
		dom.Element("meta", dom.Attr{
			"name": "viewport", "content": "width=device-width, initial-scale=1.0"}),
		dom.TextNode("\n"),
		dom.Elem("title", dom.TextNode(title)),
		dom.TextNode("\n"),
		dom.Element("link", dom.Attr{"rel": "icon", "href": siteConfig.Icon}),
		dom.TextNode("\n"),
		dom.Elem("style", dom.TextNode(siteConfig.Style)),
		dom.TextNode("\n"),
		dom.Comment("\n"+siteConfig.Copyright + " " + siteConfig.License+"\n"),
		dom.TextNode("\n"),
	)
}
