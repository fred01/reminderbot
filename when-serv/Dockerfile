FROM golang:latest as builder
WORKDIR /go/src/when_srv
RUN go get -d -v google.golang.org/grpc
RUN go get -d -v github.com/olebedev/when
COPY when_srv.go  .
COPY generated .
RUN CGO_ENABLED=0 GOOS=linux go build -a -installsuffix cgo -o when_srv .

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /go/src/when_srv/when_srv .
EXPOSE 50051
CMD ["./when_srv"]