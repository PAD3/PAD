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
	conn := pool.Get()
	r = &Redis{conn}
	defer conn.Close()

	nodes := []string{"padlab.herokuapp.com", "padlab2.herokuapp.com"}
	handler := &proxyHandler{ports: nodes}
	http.HandleFunc("/", handler.handle)

	log.Fatal(http.ListenAndServe(":"+os.Getenv("PORT"), nil))
}
