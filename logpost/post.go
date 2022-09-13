// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.
package logpost

import (
	"bufio"
	"bytes"
	"io"
	"strings"
	"time"
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

const TimestampFormat = "2006-01-02 15:04:05Z07:00 (MST)"

func (p Post) LongId() string {
	return p.Time.Format("2006/01/02/") + p.Id
}

func Parse(src io.Reader) (post Post, err error) {
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
			post.Time, err = time.Parse(TimestampFormat, value)
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
	post.Markdown = bytes.TrimLeft( bytes.TrimRight(buffer.Bytes(), " \t\n"), "\n")
	err = scanner.Err()
	return
}
