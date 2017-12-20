package main

import (
	"testing"
	"net/http"
	"net/http/httptest"
	"fmt"
	"github.com/stretchr/testify/assert"
	"time"
	"reflect"
)

type testServerNode struct{}

func (h testServerNode) ServeHTTP(w http.ResponseWriter, r *http.Request) {
	fmt.Fprintf(w, r.URL.String()+":"+r.Header.Get("Accept"))
}

func TestRunProxyOk(t *testing.T) {
	t.Parallel()
	assert := assert.New(t)

	var err error
	r, err = newRedis("redis://sylar:@localhost:6379/0")
	assert.Nil(err)

	req, err := http.NewRequest(http.MethodGet, "/google?a=2&b=4", nil)
	assert.Nil(err)

	node := testServerNode{}
	go http.ListenAndServe(":9090", node)
	go http.ListenAndServe(":9091", node)

	// Give some time for gorutines to start listening
	time.Sleep(1 * time.Second)

	rr := httptest.NewRecorder()
	testHandler := proxyHandler{ports: []string{"9090", "9091"}}
	handler := http.HandlerFunc(testHandler.handle)

	handler.ServeHTTP(rr, req)

	assert.Equal(http.StatusOK, rr.Code, "Response code should be 200 OK")
	assert.Equal("/google?a=2&b=4:", rr.Body.String(), "Expect to receive same body back")

	rr2 := httptest.NewRecorder()
	handler.ServeHTTP(rr2, req)
	assert.Equal(http.StatusOK, rr2.Code, "Response code should be 200 OK")
	assert.Equal("/google?a=2&b=4:", rr2.Body.String(), "Expect to receive same body back")
}

func TestWrongMethod(t *testing.T) {
	t.Parallel()
	assert := assert.New(t)

	var err error
	r, err = newRedis("redis://sylar:@localhost:6379/0")
	assert.Nil(err)

	req, err := http.NewRequest(http.MethodPost, "/google?a=2&b=4", nil)
	assert.Nil(err)

	node := testServerNode{}
	go http.ListenAndServe(":9090", node)
	go http.ListenAndServe(":9091", node)

	// Give some time for gorutines to start listening
	time.Sleep(1 * time.Second)

	rr := httptest.NewRecorder()
	testHandler := proxyHandler{ports: []string{"9090", "9091"}}
	handler := http.HandlerFunc(testHandler.handle)

	handler.ServeHTTP(rr, req)

	assert.Equal(http.StatusOK, rr.Code, "Response code should be 200 OK")
	assert.Equal("/google?a=2&b=4:", rr.Body.String(), "Expect to receive same body back")
}

func TestCopyHeaders(t *testing.T) {
	t.Parallel()
	assert := assert.New(t)

	sourceRequest, e := http.NewRequest(http.MethodGet, "test", nil)
	assert.Nil(e)
	sourceRequest.Header.Set("key", "value")
	sourceHeader := sourceRequest.Header

	destinationRequest, err := http.NewRequest(http.MethodGet, "test", nil)
	assert.Nil(err)
	destinationHeader := destinationRequest.Header
	copyHeaders(sourceHeader, &destinationHeader)

	equal := reflect.DeepEqual(sourceHeader, destinationHeader)
	assert.True(equal)
}