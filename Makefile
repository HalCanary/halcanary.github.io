all:
	src/make-old-pages
	src/make-redirects
	src/make-pages
	theblog

theblog: blog
	./blog .

blog: $(wildcard cmd/blog/*)
	go build ./cmd/blog

.PHONY: theblog all
