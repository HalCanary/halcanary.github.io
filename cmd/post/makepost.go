package main

import (
	"encoding/xml"
	"os"
	"path/filepath"
	"strconv"
	"time"

	"github.com/HalCanary/halcanary.github.io/check"
	"github.com/HalCanary/halcanary.github.io/logpost"

	"os/exec"
	"syscall"
)

type Blog struct {
	Copyright string
	Author    string
}

func main() {
	ex, err := os.Executable()
	check.Check(err)
	blogRoot := filepath.Dir(ex)
	config, err := os.Open(filepath.Join(blogRoot, "Blog.xml"))
	check.Check(err)
	var blog Blog
	err = xml.NewDecoder(config).Decode(&blog)
	config.Close()
	check.Check(err)
	files, err := filepath.Glob(filepath.Join(blogRoot, "src", "BlogSrc", "*.md"))
	check.Check(err)
	var maxId int
	for _, path := range files {
		f, err := os.Open(path)
		check.Check(err)
		p, err := logpost.Parse(f)
		f.Close()
		check.Check(err)
		if id, e := strconv.Atoi(p.Id); e == nil && id > maxId {
			maxId = id
		}
	}
	postId := strconv.Itoa(maxId + 1)
	now := time.Now()
	dst := filepath.Join(blogRoot, "src", "BlogSrc", now.Format("2006-01-02")+"-"+postId+".md")
	o, err := os.Create(dst)
	check.Check(err)
	defer o.Close()
	for _, s := range []string{
		"COPYRIGHT=", blog.Copyright, "\n",
		"AUTHOR=", blog.Author, "\n",
		"DATE=", now.Format(logpost.TimestampFormat), "\n",
		"POSTID=", postId, "\n",
		"TITLE=\n",
		"CATEGORIES=\n",
		"SUMMARY=\n\n\n",
	} {
		o.WriteString(s)
	}
	os.Stdout.WriteString(dst + "\n")
	check.Check(execv("vim", dst))
}

func execv(cmd string, args ...string) error {
	path, err := exec.LookPath(cmd)
	if err != nil {
		return err
	}
	return syscall.Exec(path, append([]string{cmd}, args...), syscall.Environ())
}
