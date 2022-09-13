package main

// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.

import (
	"crypto/sha256"
	"encoding/hex"
	"encoding/json"
	"image"
	_ "image/gif"
	_ "image/jpeg"
	_ "image/png"
	"log"
	"net/http"
	"os"
	"path/filepath"
	"strconv"
	"strings"

	"github.com/HalCanary/booker/dom"
	"golang.org/x/net/html"
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
			var w, h int
			var e error
			if path == "" {
				w, h, e = urlImageSize(src, "/tmp")
			} else {
				w, h, e = getImageSize(path)
			}
			if e != nil {
				log.Println(path, e)
			} else {
				node.Attr = append(node.Attr,
					html.Attribute{Key: "width", Val: strconv.Itoa(w)},
					html.Attribute{Key: "height", Val: strconv.Itoa(h)},
				)
			}
		}
	}
	for c := node.GetFirstChild(); c != nil; c = c.GetNextSibling() {
		addImageSize(c, urlBase, pathBase)
	}
}

type sizeCache struct {
	U string
	W int
	H int
}

func urlImageSize(url, dir string) (int, int, error) {
	uhashbytes := sha256.Sum256([]byte(url))
	path := filepath.Join(dir, hex.EncodeToString(uhashbytes[:]))
	f, err := os.Open(path)
	if err == nil {
		defer f.Close()
		var sc sizeCache
		if json.NewDecoder(f).Decode(&sc) == nil {
			if url == sc.U {
				return sc.W, sc.H, nil
			}
		}
	}
	resp, err := http.Get(url)
	if err != nil {
		return 0, 0, err
	}
	defer resp.Body.Close()
	if resp.StatusCode > 299 {
		log.Printf("%q: %d %q\n", url, resp.StatusCode, resp.Status)
	}
	var img image.Image
	img, _, err = image.Decode(resp.Body)

	var size image.Point
	if img != nil {
		size = img.Bounds().Size()
		f, e := os.Create(path)
		if e != nil {
			log.Println(e)
		} else {
			defer f.Close()
			sc := sizeCache{U: url, W: size.X, H: size.Y}
			e = json.NewEncoder(f).Encode(&sc)
			if e != nil {
				log.Println(e)
			}
		}
	}
	return size.X, size.Y, err
}
