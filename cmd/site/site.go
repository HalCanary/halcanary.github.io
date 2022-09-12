// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.
package main

import (
	"io/fs"
	"log"
	"os"
	"path/filepath"
	"regexp"
	"strings"
	"sync"

	"github.com/HalCanary/booker/dom"
	"github.com/HalCanary/halcanary.github.io/check"
	"github.com/HalCanary/halcanary.github.io/commonmarker"
	"github.com/HalCanary/halcanary.github.io/filebuf"
)

var (
	h1Regexp         = regexp.MustCompile(`(?s)<h1>(.*?)</h1>`)
	nosvg            = regexp.MustCompile(`/(SSL|mathapplets|u)/`)
	waitgroup        sync.WaitGroup
	changedFiles     []string
	changedFilesChan chan string
)

func main() {
	log.SetFlags(log.Lshortfile)
	if len(os.Args) < 2 {
		log.Fatal("usage?")
		return
	}

	rootPath := os.Args[1]
	sourceDir := filepath.Join(rootPath, "src", "pages")
	dstDir := filepath.Join(rootPath, "docs")
	siteConfig, err := ReadSiteConfig(rootPath)
	check.Check(err)

	changedFilesChan = make(chan string)
	go func() {
		for s := range changedFilesChan {
			changedFiles = append(changedFiles, s)
		}
	}()

	var files []string
	check.Check(filepath.WalkDir(sourceDir, func(path string, d fs.DirEntry, err error) error {
		if !d.IsDir() && strings.HasSuffix(d.Name(), ".md") {
			relpath, err := filepath.Rel(sourceDir, path)
			if err != nil {
				return err
			}
			files = append(files, relpath)
		}
		return nil
	}))

	for _, path := range files {
		dst := filepath.Join(dstDir, strings.TrimSuffix(path, ".md"), "index.html")
		waitgroup.Add(1)
		go process(siteConfig, filepath.Join(sourceDir, path), dst, false)
	}
	waitgroup.Add(1)
	go process(siteConfig, "src/index.md", filepath.Join(dstDir, "index.html"), true)
	waitgroup.Add(1)
	go process(siteConfig, "src/resume.md", filepath.Join(dstDir, "Hal_Canary_Resume.html"), false)

	waitgroup.Wait()
	if len(changedFiles) == 0 {
		log.Printf("nothing changed.")
	} else {
		log.Printf("%d changed files:\n%s\n", len(changedFiles), strings.Join(changedFiles, "\n"))
	}
}

func process(siteConfig SiteConfig, src, dst string, rootPage bool) {
	source, err := os.ReadFile(src)
	check.Check(err)
	title := ""
	markdowner := commonmarker.Markdowner()
	html := markdowner.RenderToString(source)
	h1matches := h1Regexp.FindStringSubmatch(html)
	if len(h1matches) == 2 {
		title = h1matches[1]
	}
	var bannerNode *dom.Node
	if rootPage {
		bannerNode = dom.Elem("header",
			dom.TextNode("\n"),
			dom.Elem("h1",
				dom.TextNode("\n"),
				dom.RawHtml(siteConfig.Banner),
				dom.TextNode("\n"),
			),
			dom.TextNode("\n"),
		)
	} else if !nosvg.MatchString(dst) {
		bannerNode = dom.Element("div", dom.Attr{"class": "tophead", "role": "banner"},
			dom.TextNode("\n"),
			dom.RawHtml(siteConfig.Banner),
			dom.TextNode("\n"),
		)
	}
	node := dom.Element("html", dom.Attr{"lang": siteConfig.Language},
		dom.TextNode("\n"),
		siteConfig.MakeHead(title),
		dom.TextNode("\n"),
		dom.Elem("body",
			dom.TextNode("\n"),
			bannerNode,
			dom.TextNode("\n"),
			dom.Element("div", dom.Attr{"role": "main"},
				dom.TextNode("\n"),
				dom.RawHtml(html),
				dom.TextNode("\n"),
			),
			dom.TextNode("\n"),
		),
		dom.TextNode("\n"),
	)
	f := filebuf.FileBuf{Path: dst}
	node.RenderHTML(&f)
	check.Check(f.Close())
	if f.Changed() {
		changedFilesChan <- f.Path
	}
	waitgroup.Done()
}
