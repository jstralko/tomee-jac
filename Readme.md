# TomEE Forwarding Demo

## To Run:
```bash
./build.sh && ./run.sh
```

## Servlet to Servlet forward (works)
```bash
curl -v http://localhost:8080/web1/webapp1
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /web1/webapp1 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200
< Content-Type: text/plain;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Wed, 03 Jul 2019 18:10:04 GMT
< Server: Apache TomEE
<
* Connection #0 to host localhost left intact
Hello From Webapp2 Servlet!%
```
## Servlet to Rest Resource (doesn't work)
```bash
curl -v 'http://localhost:8080/web1/webapp1?op=foo'
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /web1/webapp1?op=foo HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 404
< Content-Type: text/html;charset=utf-8
< Content-Language: en
< Content-Length: 1119
< Date: Wed, 03 Jul 2019 18:09:23 GMT
< Server: Apache TomEE
<
* Connection #0 to host localhost left intact
<!doctype html><html lang="en"><head><title>HTTP Status 404 – Not Found</title><style type="text/css">h1 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:22px;} h2 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:16px;} h3 {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;font-size:14px;} body {font-family:Tahoma,Arial,sans-serif;color:black;background-color:white;} b {font-family:Tahoma,Arial,sans-serif;color:white;background-color:#525D76;} p {font-family:Tahoma,Arial,sans-serif;background:white;color:black;font-size:12px;} a {color:black;} a.name {color:black;} .line {height:1px;background-color:#525D76;border:none;}</style></head><body><h1>HTTP Status 404 – Not Found</h1><hr class="line" /><p><b>Type</b> Status Report</p><p><b>Message</b> &#47;web2&#47;webapp2&#47;hello</p><p><b>Description</b> The origin server did not find a current representation for the target resource or is not willing to disclose that one exists.</p><hr class="line" /><h3>Apache Tomcat (TomEE)/9.0.12 (8.0.0-M2)</h3></body></html>%
```
