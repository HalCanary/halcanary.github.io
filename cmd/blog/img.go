package main
// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.

import (
	"os"
	"strings"
	"strconv"
	"image"
	 _ "image/png"
	 _ "image/jpeg"
	 _ "image/gif"

	"github.com/HalCanary/booker/dom"
	"golang.org/x/net/html"
	
	"log"
)

func getImageSize(path string) (int, int, error) {
	var size image.Point
	f, err := os.Open(path)
	if err == nil {
		defer f.Close()
		var img image.Image
		img, _, err = image.Decode(f)
		if img != nil {
			size = img.Bounds().Size()
		}
	}
	return size.X, size.Y, err
}

func getLocalPath(url, urlBase, pathBase string) string {
	if strings.HasPrefix(url, urlBase) {
		return strings.Join([]string{pathBase, "/", url[len(urlBase):]}, "")
	}
	if strings.HasPrefix(url, "/") {
		return pathBase + url[:]
	}
	return ""
}

func addImageSize(node *dom.Node, urlBase, pathBase string) {
	if node.Type == html.ElementNode && node.Data == "img" {
		var src string
		for _, attr := range node.Attr {
			if attr.Namespace == "" && attr.Key == "src" {
				src = attr.Val
				break
			}
		}
		if src != "" && !strings.HasSuffix(src, ".svg") {
			path := getLocalPath(src, urlBase, pathBase)
			if path == "" {
				log.Printf(src)
			}
			if path != "" {
				w, h, e := getImageSize(path)
				if e != nil {
					log.Println(path, e)
				}
				if e == nil {
					node.Attr = append(node.Attr,
						html.Attribute{Key:"width", Val:strconv.Itoa(w)},
						html.Attribute{Key:"height", Val:strconv.Itoa(h)},
					)
				}
			}
		}
	}
	for c := node.GetFirstChild(); c != nil; c = c.GetNextSibling() {
		addImageSize(c, urlBase, pathBase)
	}
}
