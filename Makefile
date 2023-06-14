all: theblog staticsite bin/post

theblog: bin/blog $(shell find src/BlogSrc -type f)
	./bin/blog .

staticsite: bin/site $(shell find src/pages -type f)
	./bin/site .

listcats: bin/blog $(shell find src/BlogSrc -type f)
	./bin/blog -cats .

commit-and-push:
	./src/commit-and-push

make-a-blog-entry:
	./src/make-a-blog-entry

clean:
	rm -f bin/post bin/blog bin/site

bin/post bin/blog bin/site: %: $(wildcard cmd/*/* check/* filebuf/* commonmarker/* logpost/*)
	mkdir -p ./bin
	go build -o ./bin ./cmd/$(notdir $*)

.PHONY: theblog all staticsite listcats commit-and-push make-a-blog-entry clean
