// Copyright 2022 Hal Canary
// Use of this program is governed by the file LICENSE.
package check

import (
	"log"
	"os"
)

func Check(err error) {
	if err != nil {
		log.Output(2, err.Error())
		os.Exit(1)
	}
}

func Assert(condition bool) {
	if !condition {
		log.Output(2, "Assertial Failure")
		os.Exit(1)
	}
}
