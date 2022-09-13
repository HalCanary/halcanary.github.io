all: theblog staticsite post

theblog: blog $(shell find src/BlogSrc -type f)
	./blog .

staticsite: site $(shell find src/pages -type f)
	./site .

post blog site: %: $(wildcard cmd/*/* check/* filebuf/* commonmarker/* logpost/*)
	go build ./cmd/$*

.PHONY: theblog all staticsite
