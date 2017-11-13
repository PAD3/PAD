package main

import (
	"net/http"
	"log"
	"net/http/httputil"
	"github.com/garyburd/redigo/redis"
	"io/ioutil"
	"strings"
)

type transport struct {
	http.RoundTripper
}

var count = 1

func runReverseProxy(servers []string, r *http.Request, w http.ResponseWriter) {
	director := func(request *http.Request) {
		copyHeaders(request.Header, &r.Header)
		request.URL.Scheme = "http"
		request.URL.Host = "localhost:" + servers[count]

		if count++; count >= len(servers) {
			count = 1
		}
	}
	proxy := &httputil.ReverseProxy{
		Director:  director,
		Transport: &transport{http.DefaultTransport},
	}
	proxy.ServeHTTP(w, r)
}

func copyHeaders(source http.Header, dest *http.Header) {
	for n, v := range source {
		for _, vv := range v {
			dest.Add(n, vv)
		}
	}
}

func (t *transport) RoundTrip(req *http.Request) (resp *http.Response, err error) {

	if req.Method == http.MethodGet {
		cachedResponse, e := r.get(req)
		if e == redis.ErrNil {
			log.Printf("No cache for %s in Redis", req.URL.String())
			return executeRequestAndSave(req, t)
		}

		log.Println("Using cache " + cachedResponse)
		return makeResponse(cachedResponse)
	}

	return roundTrip(req, t)
}

func makeResponse(cachedResponse string) (resp *http.Response, err error) {
	response := &http.Response{
		StatusCode: http.StatusOK,
		Body:       ioutil.NopCloser(strings.NewReader(cachedResponse)),
	}
	return response, nil
}

func executeRequestAndSave(req *http.Request, t *transport) (resp *http.Response, err error) {
	resp, err = roundTrip(req, t)

	var set string
	if resp.StatusCode == http.StatusOK {
		set = r.set(req, resp, 5)
		log.Println("Added to cache " + set)
	}

	return &http.Response{
		StatusCode: resp.StatusCode,
		Body:       ioutil.NopCloser(strings.NewReader(set)),
	}, nil
}

func roundTrip(req *http.Request, t *transport) (resp *http.Response, err error) {
	resp, err = t.RoundTripper.RoundTrip(req)
	if err != nil {
		log.Println("Could not connect to server", err)
	}
	return
}