package main

import (
	"net/http"
	"log"
)

var r *Redis

func main() {
	var err error
	r, err = newRedis("redis://sylar:@localhost:6379/0")
	if err != nil {
		log.Fatal("Cpuld not connect to Redis")
	}

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		runReverseProxy(r, w)
	})

	log.Fatal(http.ListenAndServe(":8081", nil))
}
