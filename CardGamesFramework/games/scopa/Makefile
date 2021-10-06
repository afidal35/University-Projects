CC = g++
CFLAGS= --std=c++11 -Wall
PREFIX = ../..

# Paths needed
MODEL_INCLUDE = -I $(PREFIX)/framework/include/model/game \
-I $(PREFIX)/framework/src/model/game \
-I $(PREFIX)/framework/src/model/objects \
-I $(PREFIX)/framework/include/model/objects \
-I $(PREFIX)/framework/src/model/others \
-I $(PREFIX)/framework/include/model/others

VIEW_INCLUDE = -I $(PREFIX)/framework/include/view -I $(PREFIX)/framework/src/view
CONTROLLER_INCLUDE = -I $(PREFIX)/framework/include/controller -I $(PREFIX)/framework/src/controller
EXT_INCLUDE = -I $(PREFIX)/framework/include/exception -I $(PREFIX)/framework/src/exception

# Includes
INC = $(MODEL_INCLUDE) $(VIEW_INCLUDE) $(CONTROLLER_INCLUDE) $(EXT_INCLUDE)

# Add other needed libraries here
LIBS = #-lcardgameslib

# Target
TARGET = Scopa

# Sources
MODEL_SOURCES = $(wildcard $(PREFIX)/framework/src/model/*/*.cpp) $(wildcard $(PREFIX)/framework/include/model/*/*.cpp)
VIEW_SOURCES = $(wildcard $(PREFIX)/framework/src/view/*.cpp)
MAIN = $(wildcard *.cpp)

SOURCES := $(MODEL_SOURCES) $(VIEW_SOURCES) $(MAIN)
OBJS := $(patsubst %.cpp,%.o,$(SOURCES))
DEPENDS := $(patsubst %.cpp,%.d,$(SOURCES))

all: $(TARGET)

$(TARGET): $(OBJS)
	@printf "== everything linked %s ==\n" $@
	@$(CC) $(CFLAGS) $(INC) $^ -o $@ $(LIBS)

-include $(DEPENDS)

%.o: %.cpp Makefile
	@printf "== linking %s ==\n" $@
	@$(CC) $(CFLAGS) $(INC) -MMD -MP -c $< -o $@

.PHONY:clean

clean:
	rm -rf $(TARGET) $(OBJS) $(DEPENDS)