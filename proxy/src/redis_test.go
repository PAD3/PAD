package main

import (
	"testing"
	"net/http"
	"strings"
	"io/ioutil"
	"bytes"
)

func TestNewRedis(t *testing.T) {
	t.Parallel()
	_, err := newRedis("redis://sylar:@localhost:6379/0")
	if err != nil {
		t.Fatal("Could not get redis wrapper")
	}
}

func TestGet(t *testing.T) {
	t.Parallel()

	r, err := newRedis("redis://sylar:@localhost:6379/0")
	if err != nil {
		t.Fatal("Could not get redis wrapper")
	}
	request, e := http.NewRequest(http.MethodGet, "http://www.test.com", strings.NewReader("some data"))
	if e != nil {
		t.Fatal("Could not create mock request")
	}

	resp := &http.Response{
		Body: ioutil.NopCloser(bytes.NewBufferString("some data")),
	}
	r.set(request, resp, 5)

	value, err := r.get(request)
	if err != nil {
		t.Fatal("Could not get cache from Redis")
	}

	if value != "some data" {
		t.Fatalf("Cached response %s does not equals received response %s", value, "some data")
	}
}
