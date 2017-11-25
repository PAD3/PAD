package main

import (
	"github.com/garyburd/redigo/redis"
	"log"
	"net/http"
	"io/ioutil"
	"os"
)

// Redis custom wrapper for github.com/garyburd/redigo/redis
type Redis struct {
	conn redis.Conn
}

func newPool() *redis.Pool {
	return &redis.Pool{
		MaxIdle: 80,
		MaxActive: 12000, // max number of connections
		Dial: func() (redis.Conn, error) {
			c, err := redis.DialURL(os.Getenv("REDISTOGO_URL"))
			if err != nil {
				panic(err.Error())
			}
			return c, err
		},
	}

}

var pool = newPool()

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
