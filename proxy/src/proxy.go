package main

import (
	"net/http"
	"log"
	"net/http/httputil"
	"github.com/garyburd/redigo/redis"
	"io/ioutil"
	"strings"
	"github.com/golang/gddo/httputil/header"
)

type transport struct {
	http.RoundTripper
}

var acceptableMimeTypes = [...]string{"application/json", "application/xml", "application/x-yaml"}
var count = 0

func runReverseProxy(servers []string, r *http.Request, w http.ResponseWriter) {
	director := func(request *http.Request) {
		request.Header = header.Copy(r.Header)
		request.URL.Scheme = "http"
		request.URL.Host = servers[count]
		request.Host = servers[count]

		if count++; count >= len(servers) {
			count = 0
		}
	}
	proxy := &httputil.ReverseProxy{
		Director:  director,
		Transport: &transport{http.DefaultTransport},
	}
	proxy.ServeHTTP(w, r)
}

func (t *transport) RoundTrip(req *http.Request) (resp *http.Response, err error) {

	if req.Method == http.MethodGet {
		preferredContentType := getPreferredContentType(req)

		if !isSupportedType(preferredContentType.Value) {
			log.Printf("Not acceptable MIME type %s", preferredContentType.Value)
			return &http.Response{
				StatusCode: http.StatusNotAcceptable,
				Body:       ioutil.NopCloser(strings.NewReader("")),
			}, nil
		}

		req.Header.Set("Accept", preferredContentType.Value)
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

func getPreferredContentType(req *http.Request) header.AcceptSpec {
	specs := header.ParseAccept(req.Header, "Accept")
	var max header.AcceptSpec

	for _, spec := range specs {
		if spec.Q > max.Q {
			max = spec
		}
	}
	return max
}

func isSupportedType(str string) bool {
	for _, a := range acceptableMimeTypes {
		if a == str {
			return true
		}
	}
	return false
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
