all: theblog staticsite
	src/make-old-pages
	src/make-redirects

theblog: blog $(shell find src/BlogSrc -type f)
	time ./blog .

staticsite: site $(shell find src/pages -type f)
	time ./site .

blog site: %: $(wildcard cmd/*/* check/* filebuf/* commonmarker/*)
	go build ./cmd/$*

.PHONY: theblog all staticsite
