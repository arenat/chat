# chat

Чат реализован из трех подсистем.
1. Серверная сторона (Java) - принимает входящие соединения от клиента, выполняет запросы в подсистему, в которой хранятся сообщения. Транслирует сообщения для всех пользователей определенной комнаты. (запуск binary/chat_jar/start.sh) 
В config.properties можно задать порт сервера, максимальную длину сообщений, хост и порт хранилища сообщений.

2. Клиентская сторона (Java) - при подсоединении получает историю сообщений для комнаты, которая прописана в конфиге. (запуск binary/chat_client_jar/start.sh)
В config.properties нужно задать порт и хост сервера, имя пользователя, комнату.
Если имя пользователя уже существует или отсутствует комната на сервере, то сервер отправит соответствующее сообщение и закроет соединение.

3. Хранилище сообщений (Python) - "общается" с серверной стороной, имеет три "ручки" add_new_message - для добавление нового сообщение в хранилище, в ходе добавления контролируется количество сообщений; get_all_messages - возвращает все сообщение для определенной комнаты; get_room_list - используя эту ручку сервер проверяет существование комнаты на сервере (сервис запускается вместе с сервером).
Код: message_history.py

Использованные библиотеки: netty, flask, appache.httpcomponents, json.



Roadmap на случай высоких нагрузок:
1. Для обеспечения надежности работы сервиса, который взаимодействует с клиентами, его следует сделать распределенным - причем сервис сам по себе не должен хранить текущее состояние (подсоединенные пользователи). Чтобы получать список текущих пользователей необходим еще один сервис (который будет также распределенным), по которому можно узнать текущих пользователей какой-либо комнаты (чтобы затем отправить им сообщения).

2. Сами сообщения пользователей будут "отстреливать" в очередь, а у очереди будет консумер сообщений, который будет смотреть команту и имя пользователя и затем уже оповещать нужных клиентов о том, что пришло сообщение. 

3. Отказоустойчивость следует обеспечить очередями, репликами баз данных.

4. Для хранения больших объемов данных (сообщений за 1 год) следует использовать распределенные базы данных.
