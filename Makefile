all: theblog staticsite bin/post

theblog: bin/blog $(shell find src/BlogSrc -type f)
	./bin/blog .

staticsite: bin/site $(shell find src/pages -type f)
	./bin/site .

listcats: bin/blog $(shell find src/BlogSrc -type f)
	./bin/blog -cats .

commit-and-push: all
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

~/go/bin/poststatus: go.mod
	@mkdir -p $(dir $@)
	GOBIN=$(dir $@) go install github.com/HalCanary/mastodoner/cmd/poststatus

poststatus: ~/go/bin/poststatus

.PHONY: all clean commit-and-push listcats post poststatus staticsite theblog update-deps
