package main

import (
	"log"
	"fmt"
	"gopkg.in/telegram-bot-api.v4"
	"google.golang.org/api/iterator"
	"github.com/olebedev/when"
	"github.com/mileusna/crontab"
)

func main() {
	reminderDao := New()
	defer reminderDao.client.Close()

	ctab := crontab.New()


	bot, err := tgbotapi.NewBotAPI("521757582:AAFx9NWGSU81cxH2l4eFPXNWtEm0nTfqxsg")
	if err != nil {
		log.Panic(err)
	}
	bot.Debug = true



	iter := client.Collection("users").Documents(ctx)
	for {
			doc, err := iter.Next()
			if err == iterator.Done {
					break
			}
			if err != nil {
					log.Fatalf("Failed to iterate: %v", err)
			}
			fmt.Println(doc.Data())
	}
	
	log.Printf("Authorized on account %s", bot.Self.UserName)

	u := tgbotapi.NewUpdate(0)
	u.Timeout = 60

	updates, err := bot.GetUpdatesChan(u)

	for update := range updates {
		if update.Message == nil {
			continue
		}

		log.Printf("[%s] %s", update.Message.From.UserName, update.Message.Text)
		if update.Message.Text == "Привет" {
			msg := tgbotapi.NewMessage(update.Message.Chat.ID, "Здорово!")
			msg.ReplyToMessageID = update.Message.MessageID
			bot.Send(msg)
		} else {
			msg := tgbotapi.NewMessage(update.Message.Chat.ID, update.Message.Text)
			msg.ReplyToMessageID = update.Message.MessageID
			bot.Send(msg)
		}
	}
}
