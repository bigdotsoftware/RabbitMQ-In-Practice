cd C:\RabbitMQ\rabbitmq_server-3.9.10_node2\sbin
set RABBITMQ_USE_LONGNAME=true
set ERLANG_HOME=c:\Program Files\erl-24.1.7
set RABBITMQ_NODE_IP_ADDRESS=10.0.3.15
set RABBITMQ_NODE_PORT=5682
set RABBITMQ_DIST_PORT=25682
set RABBITMQ_NODENAME=rabbit2@node2.local
set RABBITMQ_MNESIA_BASE=C:\data\rabbit2
set RABBITMQ_MNESIA_DIR=C:\data\rabbit2\data
set RABBITMQ_LOG_BASE=C:\data\rabbit2\logs

REM Change rabbit2.conf; management.tcp.port = 15682
REM The Erlang runtime automatically appends the .conf extension to the value of this variable.
set RABBITMQ_CONFIG_FILE=C:\RabbitMQ\rabbitmq_server-3.9.10_node2\config\rabbitmq
set RABBITMQ_ENABLED_PLUGINS_FILE=C:\data\rabbit2\enabled_plugins

REM rabbitmq-server.bat -detached

REM rabbitmqctl.bat --node rabbit2@node2.local stop_app
REM rabbitmqctl.bat --node rabbit2@node2.local join_cluster rabbit1@node1.local
REM rabbitmqctl.bat --node rabbit2@node2.local start_app