all: theblog
	src/make-old-pages
	src/make-redirects
	src/make-pages

theblog: blog
	time ./blog .

blog: $(wildcard cmd/blog/*)
	go build ./cmd/blog

.PHONY: theblog all
