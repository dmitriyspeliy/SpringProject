package com.game.service;


import com.game.entity.Player;
import com.game.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@SuppressWarnings("ALL")
@Component
public class PlayerService {
    @Autowired
    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }


    //GET PLAYERS LIST
    public List<Player> listOfPlayers(Map<String, String> requestParams) {

        List<Player> arrPlayer = null;

        String name = parseStringToString(requestParams.get("name"), "");
        String title = parseStringToString(requestParams.get("title"), "");
        String race = parseStringToString(requestParams.get("race"), "");
        String profession = parseStringToString(requestParams.get("profession"), "");
        Long after = parseStringToLong(requestParams.get("after"), 946573200000L);
        Long before = parseStringToLong(requestParams.get("before"), 32503568400000L);
        String banned = parseStringToString(requestParams.get("banned"), "");
        int minExperience = parseStringToInteger(requestParams.get("minExperience"), 1);
        int maxExperience = parseStringToInteger(requestParams.get("maxExperience"), 10000000);
        int minLevel = parseStringToInteger(requestParams.get("minLevel"), 1);
        int maxLevel = parseStringToInteger(requestParams.get("maxLevel"), 10000);
        String order = parseStringToString(requestParams.get("order"), "ID");
        Comparator<Player> comparator = null;
        if (order.equals("ID")) {
            comparator = Comparator
                    .comparing(Player::getId);
        } else if (order.equals("EXPERIENCE")) {
            comparator = Comparator
                    .comparing(Player::getExperience);
        } else if (order.equals("NAME")) {
            comparator = Comparator
                    .comparing(Player::getName);
        } else if (order.equals("BIRTHDAY")) {
            comparator = Comparator
                    .comparing(Player::getBirthday);
        }
        int pageNumber = parseStringToInteger(requestParams.get("pageNumber"), 0);
        int pageSize = parseStringToInteger(requestParams.get("pageSize"), 3);

        if (!requestParams.isEmpty()) {
            arrPlayer = playerRepository.findAll().stream()
                    .skip(pageNumber * pageSize)
                    .filter(x -> x.getExperience() > minExperience)
                    .filter(x -> x.getExperience() < maxExperience)
                    .filter(x -> x.getLevel() > minLevel)
                    .filter(x -> x.getLevel() < maxLevel)
                    .filter(x -> x.getName().contains(name))
                    .filter(x -> x.getBirthday().getTime() > after)
                    .filter(x -> x.getBirthday().getTime() < before)
                    .filter(x -> x.getRace().name().contains(race))
                    .filter(x -> x.getProfession().name().contains(profession))
                    .filter(x -> x.getBanned().toString().contains(banned))
                    .filter(x -> x.getTitle().contains(title))
                    .limit(pageSize)
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            arrPlayer = playerRepository.findAll().stream()
                    .limit(3).collect(Collectors.toList());
        }

        return arrPlayer;
    }

    //GET COUNT LIST
    public Long countOfPlayers(Map<String, String> requestParams) {
        long result = 0;
        if (!requestParams.isEmpty()) {
            String name = parseStringToString(requestParams.get("name"), "");
            String title = parseStringToString(requestParams.get("title"), "");
            String race = parseStringToString(requestParams.get("race"), "");
            String profession = parseStringToString(requestParams.get("profession"), "");
            Long after = parseStringToLong(requestParams.get("after"), 946573200000L);
            Long before = parseStringToLong(requestParams.get("before"), 32503568400000L);
            String banned = parseStringToString(requestParams.get("banned"), "");
            int minExperience = parseStringToInteger(requestParams.get("minExperience"), 1);
            int maxExperience = parseStringToInteger(requestParams.get("maxExperience"), 10000000);
            int minLevel = parseStringToInteger(requestParams.get("minLevel"), 1);
            int maxLevel = parseStringToInteger(requestParams.get("maxLevel"), 10000);
            result = playerRepository.findAll().stream()
                    .filter(x -> x.getExperience() > minExperience)
                    .filter(x -> x.getExperience() < maxExperience)
                    .filter(x -> x.getLevel() > minLevel)
                    .filter(x -> x.getLevel() < maxLevel)
                    .filter(x -> x.getName().contains(name))
                    .filter(x -> x.getBirthday().getTime() > after)
                    .filter(x -> x.getBirthday().getTime() < before)
                    .filter(x -> x.getRace().name().contains(race))
                    .filter(x -> x.getProfession().name().contains(profession))
                    .filter(x -> x.getBanned().toString().contains(banned))
                    .filter(x -> x.getTitle().contains(title))
                    .count();
        } else {
            result = playerRepository.count();
        }

        return result;

    }

    //Delete player
    public ResponseEntity deletePlayerById(long id) {
        if (id <= 0) {
            return new ResponseEntity("Не валидный ID", HttpStatus.BAD_REQUEST);
        } else if (id > playerRepository.count()) {
            return new ResponseEntity("Слишком больше число", HttpStatus.NOT_FOUND);
        } else if (!playerRepository.findById(id).isPresent()) {
            return new ResponseEntity("Объекта с таким индексом нет", HttpStatus.NOT_FOUND);
        } else {
            playerRepository.deleteById(id);
            return new ResponseEntity(HttpStatus.OK);
        }
    }

    //Create player
    public ResponseEntity createPlayer(Player player) {
        if (player.getName() == null || player.getTitle() == null || player.getRace() == null
                || player.getProfession() == null || player.getExperience() == null || player.getBirthday() == null) {
            return new ResponseEntity("Все поля должны быть заполнены", HttpStatus.BAD_REQUEST);
        } else if (Objects.equals(player.getName(), "")) {
            return new ResponseEntity("Имя должно быть заполено", HttpStatus.BAD_REQUEST);
        } else if (player.getExperience() < 0 || player.getExperience() > 10_000_000) {
            return new ResponseEntity("Опыт вне заданных значениях", HttpStatus.BAD_REQUEST);
        } else if (player.getBirthday().before(new Date(0))) {
            return new ResponseEntity("Дата вне заданных пределах", HttpStatus.BAD_REQUEST);
        } else if (player.getBirthday().getTime() <= 946573200000L || player.getBirthday().getTime() >= 32503568400000L) {
            return new ResponseEntity("Дата вне заданных пределах", HttpStatus.BAD_REQUEST);
        } else if (player.getName().length() > 12) {
            return new ResponseEntity("Слишком длинное имя", HttpStatus.BAD_REQUEST);
        } else if (player.getTitle().length() > 30) {
            return new ResponseEntity("Слишком длинный тайтл", HttpStatus.BAD_REQUEST);
        } else {
            player.setLevel((int) ((Math.sqrt(2500 + 200 * player.getExperience()) - 50) / 100));
            player.setUntilNextLevel(50 * (player.getLevel() + 1) * (player.getLevel() + 2) - player.getExperience());
            return new ResponseEntity(playerRepository.save(player), HttpStatus.OK);
        }
    }

    //getPlayerById
    public ResponseEntity getPlayerById(long id) {
        ResponseEntity responseEntity = null;
        if (id <= 0) {
            responseEntity = new ResponseEntity("Не валидный ID", HttpStatus.BAD_REQUEST);
        } else if (id > playerRepository.count()) {
            responseEntity = new ResponseEntity("Слишком больше число", HttpStatus.NOT_FOUND);
        } else if (!playerRepository.findById(id).isPresent()) {
            responseEntity = new ResponseEntity("Объекта с таким индексом нет", HttpStatus.NOT_FOUND);
        } else {
            playerRepository.findById(id);
            responseEntity = new ResponseEntity(playerRepository.findById(id).get(), HttpStatus.OK);
        }
        return responseEntity;
    }

    //updatePlayerById
    public ResponseEntity updatePlayerById(long id, Player player) {

        if (id == 0) {
            return new ResponseEntity("id не может быть 0", HttpStatus.BAD_REQUEST);
        }
        if (player.getBirthday() != null && player.getBirthday().before(new Date(0))) {
            return new ResponseEntity("Дата вне заданных пределах", HttpStatus.BAD_REQUEST);
        }
        if (player.getExperience() != null && player.getExperience() < 0
                || player.getExperience() != null && player.getExperience() > 10_000_000) {
            return new ResponseEntity("Опыт вне заданных значениях", HttpStatus.BAD_REQUEST);
        }
        Optional<Player> row = playerRepository.findById(id);
        if (row.isPresent()) {
            Player item = row.get();
            if (player.getName() != null) {
                item.setName(player.getName());
            }
            if (player.getTitle() != null) {
                item.setTitle(player.getTitle());
            }
            if (player.getRace() != null) {
                item.setRace(player.getRace());
            }
            if (player.getProfession() != null) {
                item.setProfession(player.getProfession());
            }
            if (player.getExperience() != null) {
                item.setExperience(player.getExperience());
            }
            if (player.getBirthday() != null) {
                item.setBirthday(player.getBirthday());
            }
            if (player.getBanned() != null) {
                item.setBanned(player.getBanned());
            }
            item.setLevel((int) ((Math.sqrt(2500 + 200 * item.getExperience()) - 50) / 100));
            item.setUntilNextLevel(50 * (item.getLevel() + 1) * (item.getLevel() + 2) - item.getExperience());
            return new ResponseEntity(playerRepository.save(item), HttpStatus.OK);
        } else {
            return new ResponseEntity("Объект с таким id не надйен", HttpStatus.NOT_FOUND);
        }
    }

    //HelpertoParse
    private static int parseStringToInteger(String value, int defaultValue) {
        return value == null || value.isEmpty() ? defaultValue : Integer.parseInt(value);
    }
    //HelpertoParse
    private static long parseStringToLong(String value, long defaultValue) {
        return value == null || value.isEmpty() ? defaultValue : Long.parseLong(value);
    }
    //HelpertoParse
    private static String parseStringToString(String value, String defaultValue) {
        return value == null || value.isEmpty() ? defaultValue : value;
    }

}




