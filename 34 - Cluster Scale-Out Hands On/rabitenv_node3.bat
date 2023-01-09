cd C:\RabbitMQ\rabbitmq_server-3.9.10_node3\sbin
set ERLANG_HOME=c:\Program Files\erl-24.1.7
set RABBITMQ_NODE_PORT=5692
set RABBITMQ_DIST_PORT=25692
set RABBITMQ_NODENAME=rabbit3@localhost
set RABBITMQ_MNESIA_BASE=C:\data\rabbit3
set RABBITMQ_MNESIA_DIR=C:\data\rabbit3\data
set RABBITMQ_LOG_BASE=C:\data\rabbit3\logs

REM Change rabbit3.conf; management.tcp.port = 15692
REM The Erlang runtime automatically appends the .conf extension to the value of this variable.
set RABBITMQ_CONFIG_FILE=C:\RabbitMQ\rabbitmq_server-3.9.10_node3\config\rabbitmq
set RABBITMQ_ENABLED_PLUGINS_FILE=C:\data\rabbit3\enabled_plugins

REM rabbitmq-server.bat -detached

REM rabbitmqctl.bat --node rabbit3@localhost stop_app
REM rabbitmqctl.bat --node rabbit3@localhost join_cluster rabbit1@localhost
REM rabbitmqctl.bat --node rabbit3@localhost start_app
