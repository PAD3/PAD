package main

import (
	"net/http"
	"log"
	"os"
)

var r *Redis

type proxyHandler struct {
	ports []string
}

func (h *proxyHandler) handle(w http.ResponseWriter, r *http.Request) {
	runReverseProxy(h.ports, r, w)
}

func main() {
	var err error
	r, err = newRedis("redis://sylar:@localhost:6379/0")
	handleError(err, "Could not connect to Redis")

	handler := &proxyHandler{ports: os.Args}
	http.HandleFunc("/", handler.handle)

	log.Fatal(http.ListenAndServe(":8081", nil))
}
