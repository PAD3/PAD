package main

import (
	"github.com/garyburd/redigo/redis"
	"log"
	"net/http"
	"io/ioutil"
)

// Redis custom wrapper for github.com/garyburd/redigo/redis
type Redis struct {
	conn redis.Conn
}

func newRedis(url string) (r *Redis, err error) {
	conn, err := redis.DialURL(url)
	return &Redis{conn}, err
}

func (r *Redis) get(req *http.Request) (value string, err error) {
	return redis.String(r.conn.Do("GET", getKey(req)))
}

func (r *Redis) set(req *http.Request, resp *http.Response, ttl int) string {

	bodyBytes, err := ioutil.ReadAll(resp.Body)
	handleError(err, "Could not read body from request ")
	bodyString := string(bodyBytes)

	result, err := redis.String(r.conn.Do("SET", getKey(req), bodyString, "EX", ttl))
	if result != "OK" {
		log.Fatal("result not ok: ", result)
	}
	handleError(err, "Could not save resp to cache ")

	return bodyString
}
func handleError(err error, text string) {
	if err != nil {
		log.Fatal(text, err)
	}
}

func getKey(req *http.Request) string {
	return req.URL.Path + req.URL.RawQuery + req.Header.Get("Accept")
}
