package main

import (
	"log"
	"fmt"
//	"github.com/mileusna/crontab"
	"gopkg.in/telegram-bot-api.v4"
	"github.com/olebedev/when"
	"github.com/olebedev/when/rules/ru"
	"github.com/olebedev/when/rules/common"
	"time"
)

func main() {
	reminderDao := New()
	defer reminderDao.client.Close()

	w := when.New(nil)

	w.Add(common.All...)
	w.Add(ru.All...)

	// ctab := crontab.New()

	bot, err := tgbotapi.NewBotAPI("521757582:AAFx9NWGSU81cxH2l4eFPXNWtEm0nTfqxsg")
	if err != nil {
		log.Panic(err)
	}
	bot.Debug = true
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
			msg := tgbotapi.NewMessage(update.Message.Chat.ID, "Привет!")
			msg.ReplyToMessageID = update.Message.MessageID
			bot.Send(msg)
		} else {			
			// msg := tgbotapi.NewMessage(update.Message.Chat.ID, update.Message.Text)
			r, err := w.Parse(update.Message.Text, time.Now())
			if err != nil {
				msg := tgbotapi.NewMessage(update.Message.Chat.ID, err.Error())
				bot.Send(msg)
			}
			if  r == nil {
				msg := tgbotapi.NewMessage(update.Message.Chat.ID, "Sorry, i don't understand you")
				bot.Send(msg)
		   } else {
				s := fmt.Sprintf(
					"the time",
					r.Time.String(),
					"mentioned in",
					update.Message.Text[r.Index:r.Index+len(r.Text)],
				)
				msg := tgbotapi.NewMessage(update.Message.Chat.ID, s)
				bot.Send(msg)
			}
		}
	}
}
