package main

import (
	"net"
	"log"
	"google.golang.org/grpc/reflection"
	"google.golang.org/grpc"
	"golang.org/x/net/context"
)

import pb "./generated"

const (
	port = ":50051"
)

// server is used to implement helloworld.GreeterServer.
type WhenServerImpl struct{}

func (ws *WhenServerImpl) Parse(context.Context, *pb.WhenRequest) (*pb.WhenResponse, error) {
	var response = &pb.WhenResponse{Message:"Test return"}
	return response, nil
}

func main() {
	lis, err := net.Listen("tcp", port)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()

	pb.RegisterWhenServer(s, &WhenServerImpl{})
	//pb.RegisterGreeterServer(s, &server{})
	// Register reflection service on gRPC server.
	reflection.Register(s)
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
