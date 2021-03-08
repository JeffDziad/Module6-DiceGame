package com.JeffDziad;

import java.util.*;
import java.util.stream.Collectors;

public class DiceGame {

    private final List<Player> players;
    private final List<Die> dice;
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls){
        players = new ArrayList<>();
        dice = new ArrayList<>();
        this.maxRolls = maxRolls;
        if(countPlayers < 2){
            throw new IllegalArgumentException();
        }else{
            for(int i = 0; i < countPlayers; i++){
                Player player = new Player();
                players.add(player);
            }
            for(int i = 0; i < countDice; i++){
                Die die = new Die(6);
                dice.add(die);
            }
        }
    }

    private boolean allDiceHeld(){
        return dice.stream().allMatch(Die::isBeingHeld);
    }

    public boolean autoHold(int faceValue){
        Optional<Die> matchHeld = dice.stream().filter(die -> die.getFaceValue() == faceValue && die.isBeingHeld()).findFirst();
        Optional<Die> matchUnheld = dice.stream().filter(die -> die.getFaceValue() == faceValue && !(die.isBeingHeld())).findFirst();
        if(matchHeld.isPresent()){
            return true;
        }else if(matchUnheld.isPresent()){
            matchUnheld.get().holdDie();
            return true;
        }else{
            return false;
        }
    }

    public boolean currentPlayerCanRoll(){
        if(currentPlayer.getRollsUsed() == maxRolls){
            return false;
        }else if(allDiceHeld()){
            return false;
        }else{
            return true;
        }
    }

    public int getCurrentPlayerNumber(){
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore(){
        return currentPlayer.getScore();
    }

    public String getDiceResults(){
        return dice.stream().map(Die::toString).collect(Collectors.joining());
    }

    public String getFinalWinner(){
        Optional<Player> finalWinner = players.stream().max(Comparator.comparingInt(Player::getWins)).stream().findFirst();
        return finalWinner.toString();
    }

    public String getGameResults(){
        List<Player> sortedList = players.stream().sorted(Comparator.comparingInt(Player::getScore).reversed()).collect(Collectors.toList());
        int highestScore = sortedList.get(0).getScore();
        sortedList.forEach(player -> {
            if(player.getScore() == highestScore){
                player.addWin();
            }else{
                player.addLoss();
            }
        });
        return sortedList.stream().map(Player::toString).collect(Collectors.joining());
    }

    private boolean isHoldingDie(int faceValue){
        Optional<Die> holdingDie = dice.stream().filter(d -> d.getFaceValue() == faceValue).findFirst();
        return holdingDie.isPresent();
    }

    public boolean nextPlayer(){
        if(currentPlayer == players.get(players.size() - 1))
        {
            return false;
        }else{
            int id = 0;
            for(Player player : players){
                if(currentPlayer == player){
                    currentPlayer = players.get(id + 1);
                    return true;
                }
                id++;
            }
        }
        return false;
    }

    public void playerHold(char dieNum){
        if(dice.stream().anyMatch(die -> die.getDieNum() == dieNum)){
            for(Die dice : dice){
                if(dice.getDieNum() == dieNum){
                    dice.holdDie();
                    break;
                }
            }
        }
    }

    public void resetDice(){
        dice.forEach(Die::resetDie);
    }

    public void resetPlayers(){
        players.forEach(Player::resetPlayer);
    }

    public void rollDice(){
        currentPlayer.roll();
        dice.forEach(Die::rollDie);
    }

    public void scoreCurrentPlayer(){
        int score = 0;
        int playerCurrentScore = currentPlayer.getScore();
        if(isHoldingDie(6) && isHoldingDie(5) && isHoldingDie(4)){
            for(Die die : dice){
                score += die.getFaceValue();
            }
            score -= 6;
            score -= 5;
            score -= 4;
            currentPlayer.setScore(playerCurrentScore + score);
        }
    }

    public void startNewGame(){
        currentPlayer = players.get(0);
        resetPlayers();
    }

}
