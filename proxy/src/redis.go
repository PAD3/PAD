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
	if err != nil {
		log.Fatal("Could not read body from request ", err)
	}
	bodyString := string(bodyBytes)

	result, err := redis.String(r.conn.Do("SET", getKey(req), bodyString, "EX", ttl))
	if result != "OK" {
		log.Fatal("result not ok: ", result)
	}
	if err != nil {
		log.Fatal("Could not save resp to cache ", err)
	}

	return bodyString
}

func getKey(req *http.Request) string {
	return req.URL.String() + ":" + req.Header.Get("Accept")
}
