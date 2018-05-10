package main

import (
	"net"
	"log"
	"google.golang.org/grpc/reflection"
	"google.golang.org/grpc"
	"golang.org/x/net/context"
)

import (
	pb "github.com/fred01/reminderbot/when_srv/generated"
	"github.com/olebedev/when"
	"github.com/olebedev/when/rules/common"
	"github.com/olebedev/when/rules/ru"
	"time"
	"fmt"
	"github.com/olebedev/when/rules/en"
)

const (
	port = ":50051"
)

// server is used to implement helloworld.GreeterServer.
type WhenServerImpl struct {
	w *when.Parser
}

func (ws *WhenServerImpl) Parse(c context.Context, r *pb.WhenRequest) (*pb.WhenResponse, error) {
	log.Printf("Request to parse string '%s'", r.Name)

	parsed, err := ws.w.Parse(r.Name, time.Now())
	if err != nil {
		return &pb.WhenResponse{Message: fmt.Sprintf("ERROR-1: Error while parsing [%s]",err.Error())}, nil
	} else {
		if parsed != nil {
			s := fmt.Sprintf("%s/%s",parsed.Time.Format(time.RFC3339), r.Name[parsed.Index:parsed.Index+len(parsed.Text)])
			return &pb.WhenResponse{Message: s}, nil
		} else {
			return &pb.WhenResponse{Message: fmt.Sprintf("ERROR-2: String doesn't have date mentioned")}, nil
		}
	}
}

func main() {
	log.Printf("Initializing server")
	w := when.New(nil)
	w.Add(common.All...)
	w.Add(ru.All...)
	w.Add(en.All...)

	whenServer := &WhenServerImpl{w: w}

	lis, err := net.Listen("tcp", port)
	if err != nil {
		log.Fatalf("failed to listen: %v", err)
	}
	s := grpc.NewServer()
	pb.RegisterWhenServer(s, whenServer)
	reflection.Register(s)
	if err := s.Serve(lis); err != nil {
		log.Fatalf("failed to serve: %v", err)
	}
}
