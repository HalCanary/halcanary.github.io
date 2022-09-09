package main

import (
	"bytes"
	"os"
	"path/filepath"
)

type BufferedFile struct {
	Path    string
	buf     bytes.Buffer
	changed bool
}

func (b *BufferedFile) Write(p []byte) (int, error) { return b.buf.Write(p) }

func (b *BufferedFile) WriteString(s string) (int, error) { return b.buf.WriteString(s) }

func (b *BufferedFile) Reset() error {
	b.buf = bytes.Buffer{}
	return nil
}

func (b *BufferedFile) Len() int { return b.buf.Len() }

func (b *BufferedFile) Close() error {
	d := b.buf.Bytes()
	b.buf = bytes.Buffer{}
	c, e := os.ReadFile(b.Path)
	if e == nil && bytes.Equal(c, d) {
		return nil
	}
	if e = os.MkdirAll(filepath.Dir(b.Path), 0o777); e != nil {
		return e
	}
	f, e := os.Create(b.Path)
	if e != nil {
		return e
	}
	b.changed = true
	_, e = f.Write(d)
	_ = f.Close()
	return e
}

func (b BufferedFile) Changed() bool { return b.changed }
