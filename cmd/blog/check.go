package main

import (
	"log"
	"os"
)

func check(err error) {
	if err != nil {
		log.Output(2, err.Error())
		os.Exit(1)
	}
}

func assert(condition bool) {
	if !condition {
		log.Output(2, "Assertial Failure")
		os.Exit(1)
	}
}
