package com.game.controller;

import com.game.entity.Player;
import com.game.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("rest/players")
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @GetMapping()
    public List<Player> getAllPlayers(@RequestParam(required = false) Map<String, String> requestParams) {
        return playerService. listOfPlayers(requestParams);
    }

    @GetMapping("/count")
    public Long getCountPlayers(@RequestParam(required = false) Map<String, String> requestParams) {
        return playerService.countOfPlayers(requestParams);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPlayerById(@PathVariable int id) {
        return playerService.getPlayerById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deletePlayerById(@PathVariable long id) {
        return playerService.deletePlayerById(id);
    }

    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity createPlayer(@RequestBody Player player) {
        return playerService.createPlayer(player);
    }

    @PostMapping(value = "/{id}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public ResponseEntity updatePlayerById(@PathVariable int id, @RequestBody Player player) {
        return playerService.updatePlayerById(id, player);
    }


}
