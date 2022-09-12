all: theblog staticsite
	src/make-old-pages
	src/make-redirects

theblog: blog $(shell find src/BlogSrc -type f)
	./blog .

staticsite: site $(shell find src/pages -type f)
	./site .

blog site: %: $(wildcard cmd/*/* check/* filebuf/* commonmarker/*)
	go build ./cmd/$*

.PHONY: theblog all staticsite
