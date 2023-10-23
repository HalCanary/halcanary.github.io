package main

// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.

import (
	"encoding/xml"
	"os"

	"github.com/HalCanary/facility/dom"
)

type SiteConfig struct {
	Language  string // RFC-5646
	Copyright string // "Copyright YEAR-YEAR Legal Entity."
	License   string // "ALL RIGHTS RESERVED."
	BaseUrl   string // "https://example.com"
	Style     string // Inline CSS content.
	Icon      string // URL for icon/
	Banner    string
	Melinks   []string
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
		dom.Text("\n"),
		dom.Element("meta", dom.Attr{"charset": "utf-8"}),
		dom.Text("\n"),
		dom.Element("meta", dom.Attr{
			"name": "viewport", "content": "width=device-width, initial-scale=1.0"}),
		dom.Text("\n"),
		dom.Elem("title", dom.Text(title)),
		dom.Text("\n"),
		dom.Element("link", dom.Attr{"rel": "icon", "href": siteConfig.Icon}),
		dom.Text("\n"),
		dom.Elem("style", dom.Text(siteConfig.Style)),
		dom.Text("\n"),
		dom.Comment("\n"+siteConfig.Copyright+" "+siteConfig.License+"\n"),
		dom.Text("\n"),
	)
}

func (siteConfig SiteConfig) MakeMelinks() *dom.Node {
	var d *dom.Node
	if len(siteConfig.Melinks) > 0 {
		d = dom.Elem("div")
		for _, link := range siteConfig.Melinks {
			dom.Append(d, dom.Element("a", dom.Attr{"rel": "me", "href": link}, dom.Text("")))
		}
	}
	return d
}
