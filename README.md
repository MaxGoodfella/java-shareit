### Привет! Спасибо за проверку!

Такой вопрос: подскажи, пожалуйста, как правильно расположить плагин jacoco в pom-файлах приложения
так, чтобы test coverage учитывал только один из микросервисов (shareit-server в нашем случае). Пытался
разобраться вместе с наставником, он попытался помочь, высказав предположение, но не сработало.

Также извиняюсь за грязь в коммит-истории (очень много подводных камней оказалось, даже просил друга с
виндового устройства запушить, потому что думал, что может мой макбук приколы подкидывает)

**P.S.**

У меня проблема с часовыми поясами, из-за этого мне выдавало много ошибок в тестах, связанных
с комментариями и с бронированиями. Я не понимаю, почему разные части приложения у меня работают либо
на UTC, либо на моём местном времени, как бы я ни пытался привести более-менее адекватным способом всё
к одному часовому поясу. Пришлось использовать костыль (увидишь его в docker-compose). Ниже приложу
ссылку на облако со скриншотами (первые 3 скриншота на облаке).

[Link](https://disk.yandex.ru/d/j_JSwzqBgJ_RVg)

**P.S. (updated1)**

Когда запушил в ПР работу со своим костылём в docker-compose у меня не проходили тесты, опять по вине
часовых поясов. Наставник подсказал, теперь убрал этот костыль - тесты во время ПР проходят, но когда 
я запускаю коллекцию тестов в постмане - опять получаю то же самое, что и выше описывал (4 скриншот на облаке).

**P.S. (updated2)**

Оказалось, что проблема в постман тестах. Наставник сказал, что этому не уделили внимания при создании.
Когда он скинул исправленные, всё заработало) 