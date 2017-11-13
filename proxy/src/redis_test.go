package main

import (
	"testing"
	"net/http"
	"strings"
	"io/ioutil"
	"bytes"
	"github.com/stretchr/testify/assert"
)

func TestGet(t *testing.T) {
	t.Parallel()
	assert := assert.New(t)

	r, err := newRedis("redis://sylar:@localhost:6379/0")
	assert.Nil(err)

	request, e := http.NewRequest(http.MethodGet, "http://www.test.com", strings.NewReader("some data"))
	assert.Nil(e)

	resp := &http.Response{
		Body: ioutil.NopCloser(bytes.NewBufferString("some data")),
	}
	r.set(request, resp, 5)

	value, err := r.get(request)
	assert.Nil(err)

	assert.Equal("some data", value, "Cached data shoudl be correct")
}
