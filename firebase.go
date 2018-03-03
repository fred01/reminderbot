package main

import (
	"google.golang.org/api/option"
	"firebase.google.com/go"
	"log"
	"golang.org/x/net/context"
	"cloud.google.com/go/firestore"
)

/**
  Reminder - struct to hold 
*/
type Reminder struct {
	id string
	CronExpression string
	Message string
}

func New() (*RemindersDAO) {
	r := &RemindersDAO{}
	r.Init()
	return r
}

type RemindersDAO struct {
	client *firestore.Client
	ctx context.Context
} 

func (dao RemindersDAO) Init()  {
	ctx := context.Background()
	dao.ctx = ctx

	sa := option.WithCredentialsFile("reminder-847c22e15205.json")
	app, err := firebase.NewApp(ctx, nil, sa)
	if err != nil {
		log.Fatalln(err)
	}
	client, err := app.Firestore(ctx)
	if err != nil {
		log.Fatalln(err)
	}
	dao.client = client
}

func (dao RemindersDAO) StoreReminder(reminder Reminder)  {
	dao.client.Collection("reminders").Doc(reminder.id).Set(dao.ctx, reminder)
}

func (dao RemindersDAO) ReadAllReminders() (*firestore.DocumentIterator) {
	return dao.client.Collection("reminders").Documents(dao.ctx)		
}

