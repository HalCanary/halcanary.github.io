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

	"github.com/HalCanary/facility/dom"
	"github.com/HalCanary/halcanary.github.io/check"
	"github.com/HalCanary/halcanary.github.io/commonmarker"
	"github.com/HalCanary/halcanary.github.io/filebuf"
)

var (
	h1Regexp         = regexp.MustCompile(`(?s)<h1>(.*?)</h1>`)
	nosvg            = regexp.MustCompile(`/(SSL|mathapplets|u)/|/Hal_Canary_Resume.html$`)
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

	makeRedirects(siteConfig, rootPath)

	files, err := getFiles(sourceDir)
	check.Check(err)

	for _, path := range files {
		if !strings.HasSuffix(path, ".md") {
			continue
		}
		dst := filepath.Join(dstDir, strings.TrimSuffix(path, ".md"), "index.html")
		waitgroup.Add(1)
		go process(siteConfig, filepath.Join(sourceDir, path), dst, false, true)
	}
	waitgroup.Add(1)
	go process(siteConfig, "src/index.md", filepath.Join(dstDir, "index.html"), true, true)
	waitgroup.Add(1)
	go process(siteConfig, "src/resume.md", filepath.Join(dstDir, "Hal_Canary_Resume.html"), false, false)

	waitgroup.Wait()
	if len(changedFiles) == 0 {
		log.Printf("nothing changed.")
	} else {
		log.Printf("%d changed files:\n%s\n", len(changedFiles), strings.Join(changedFiles, "\n"))
	}
}

func process(siteConfig SiteConfig, src, dst string, rootPage bool, copyright bool) {
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
			dom.Text("\n"),
			dom.Elem("h1",
				dom.Text("\n"),
				dom.RawHtml(siteConfig.Banner),
				dom.Text("\n"),
			),
			dom.Text("\n"),
		)
	} else if !nosvg.MatchString(dst) {
		bannerNode = dom.Element("div", dom.Attr{"class": "tophead", "role": "banner"},
			dom.Text("\n"),
			dom.RawHtml(siteConfig.Banner),
			dom.Text("\n"),
		)
	}
	var copyrightBlock *dom.Node
	if copyright {
		copyrightBlock = dom.Element("div", dom.Attr{"class": "rightside"},
			dom.Text("\n"),
			dom.Elem("em", dom.Text(siteConfig.BaseUrl)),
			dom.Text("\n"),
			dom.Elem("br"),
			dom.Text("\n"),
			dom.Elem("em", dom.Text(siteConfig.Copyright)),
			dom.Text("\n"),
			dom.Elem("br"),
			dom.Text("\n"),
			dom.Elem("em", dom.Text(siteConfig.License)),
			dom.Text("\n"),
		)
	} else {
		copyrightBlock = dom.Comment(" " + siteConfig.Copyright + " " + siteConfig.License + " ")
	}
	node := dom.Element("html", dom.Attr{"lang": siteConfig.Language},
		dom.Text("\n"),
		siteConfig.MakeHead(title),
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			bannerNode,
			dom.Text("\n"),
			siteConfig.MakeMelinks(),
			dom.Text("\n"),
			dom.Element("article", dom.Attr{"role": "main"},
				dom.Text("\n"),
				dom.RawHtml(html),
				dom.Text("\n"),
			),
			dom.Text("\n"),
			copyrightBlock,
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
	f := filebuf.FileBuf{Path: dst}
	dom.RenderHTML(node, &f)
	check.Check(f.Close())
	if f.Changed() {
		changedFilesChan <- f.Path
	}
	waitgroup.Done()
}

func getFiles(dir string) ([]string, error) {
	var files []string
	err := filepath.WalkDir(dir, func(path string, d fs.DirEntry, err error) error {
		if !d.IsDir() {
			relpath, err := filepath.Rel(dir, path)
			if err != nil {
				return err
			}
			files = append(files, relpath)
		}
		return err
	})
	return files, err
}

func makeRedirects(siteConfig SiteConfig, rootPath string) error {
	redirectsDir := filepath.Join(rootPath, "src", "redirects")
	files, err := getFiles(redirectsDir)
	if err != nil {
		return err
	}
	for _, path := range files {
		data, err := os.ReadFile(filepath.Join(redirectsDir, path))
		if err != nil {
			return err
		}
		url := strings.TrimSpace(string(data))
		dst := filepath.Join(rootPath, "docs", path, "index.html")
		makeRedirect(siteConfig, url, dst)
	}
	return nil
}

func makeRedirect(siteConfig SiteConfig, url, dst string) {
	node := dom.Element("html", dom.Attr{"lang": siteConfig.Language},
		dom.Text("\n"),
		dom.Elem("head",
			dom.Text("\n"),
			dom.Element("meta", dom.Attr{"charset": "utf-8"}),
			dom.Text("\n"),
			dom.Element("meta", dom.Attr{
				"name": "viewport", "content": "width=device-width, initial-scale=1.0"}),
			dom.Text("\n"),
			dom.Elem("title", dom.Text(url)),
			dom.Text("\n"),
			dom.Elem("script", dom.Text(`window.location.replace("`+url+`");`)),
			dom.Text("\n"),
			dom.Element("link", dom.Attr{"rel": "icon", "href": siteConfig.Icon}),
			dom.Text("\n"),
			dom.Elem("style", dom.Text(`body{font-family:sans-serif;}`)),
			dom.Text("\n"),
		),
		dom.Text("\n"),
		dom.Elem("body",
			dom.Text("\n"),
			siteConfig.MakeMelinks(),
			dom.Text("\n"),
			dom.Element("a", dom.Attr{"style": "overflow-wrap:break-word", "href": url},
				dom.Text(url),
			),
			dom.Text("\n"),
		),
		dom.Text("\n"),
	)
	f := filebuf.FileBuf{Path: dst}
	dom.RenderHTML(node, &f)
	check.Check(f.Close())
	if f.Changed() {
		changedFilesChan <- f.Path
	}
}
