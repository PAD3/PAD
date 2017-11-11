package main

import (
	"net/http"
	"net/http/httputil"
	"log"
	"github.com/garyburd/redigo/redis"
	"io/ioutil"
	"strings"
)

var redisConn redis.Conn

type transport struct {
	http.RoundTripper
}

func (t *transport) RoundTrip(req *http.Request) (resp *http.Response, err error) {

	key := req.URL.String() + ":" + req.Header.Get("Accept")
	cachedResponse, err := redis.String(redisConn.Do("GET", key))
	if err == redis.ErrNil {
		log.Printf("No cache for %s in Redis", key)
		return executeRequestAndSave(key, req, t)
	}

	log.Println("Using cache " + cachedResponse)
	return getCachedResponse(cachedResponse)
}
func getCachedResponse(cachedResponse string) (resp *http.Response, err error) {
	response := &http.Response{
		StatusCode: http.StatusOK,
		Body:       ioutil.NopCloser(strings.NewReader(cachedResponse)),
	}
	return response, nil
}

func executeRequestAndSave(key string, req *http.Request, t *transport) (resp *http.Response, err error) {
	resp, err = t.RoundTripper.RoundTrip(req)
	if err != nil {
		log.Println("Could not connect to server")
		return nil, err
	}

	bodyBytes, err := ioutil.ReadAll(resp.Body)
	if err != nil {
		log.Fatal("Could not read body from request")
	}
	bodyString := string(bodyBytes)

	ttl := 5
	result, err := redis.String(redisConn.Do("SET", key, bodyString, "EX", ttl))
	if result != "OK" {
		panic("result not ok: " + result)
	}
	if err != nil {
		log.Fatal("Could not save response to cache")
	}
	log.Println("Added to cache " + bodyString)

	return &http.Response{
		StatusCode: resp.StatusCode,
		Body:       ioutil.NopCloser(strings.NewReader(bodyString)),
	}, nil
}

func main() {

	var err error
	redisConn, err = redis.DialURL("redis://sylar:@localhost:6379/0")
	if err != nil {
		log.Fatal("Could not connect to Redis")
	}

	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		runReverseProxy(r, w)
	})

	log.Fatal(http.ListenAndServe(":8081", nil))
}

func runReverseProxy(r *http.Request, w http.ResponseWriter) {
	director := func(request *http.Request) {
		log.Printf("EYE-CATCHER ON REQUEST: %s\n", r.URL.String())

		copyHeaders(request.Header, &r.Header)
		request.Host = "localhost:9090"
		request.URL.Scheme = "http"
		request.URL.Host = "localhost:9090"
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
