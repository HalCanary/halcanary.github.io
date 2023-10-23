all: theblog staticsite bin/post

theblog: bin/blog $(shell find src/BlogSrc -type f)
	./bin/blog .

staticsite: bin/site $(shell find src/pages -type f)
	./bin/site .

listcats: bin/blog $(shell find src/BlogSrc -type f)
	./bin/blog -cats .

commit-and-push:
	./src/commit-and-push

post: bin/post
	$<

clean:
	rm -f bin/post bin/blog bin/site

bin/post bin/blog bin/site: %: $(wildcard cmd/*/* check/* filebuf/* commonmarker/* logpost/*)
	mkdir -p ./bin
	go get ./...
	go build -o ./bin ./cmd/$(notdir $*)

update-deps:
	go get -u ./...
	git add go.mod go.sum

.PHONY: theblog all staticsite listcats commit-and-push post clean update-deps
