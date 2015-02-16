package com.jabyftw.gameclient.gamestates.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.util.GameDrawable;
import com.jabyftw.gameclient.util.HudDrawable;
import com.jabyftw.gameclient.util.Tickable;

import java.util.Stack;

/**
 * Created by Isa on 01/01/2015.
 */
public class GameStateManager implements Tickable, GameDrawable, HudDrawable, Disposable {

    private final Stack<PseudoGameState> pseudoGameState = new Stack<PseudoGameState>();
    private GameState mainGameState;

    public GameStateManager(GameState firstGameState) {
        setGameState(firstGameState);
    }

    public void setGameState(GameState gameState) {
        if(gameState == null) {
            if(pseudoGameState.empty()) {
                throw new IllegalArgumentException("GameState is null without having a background state.");
            } else {
                pseudoGameState.pop().dispose();

                Gdx.input.setInputProcessor(
                        pseudoGameState.empty() ?
                                (mainGameState.shouldRegisterInput() ? mainGameState.getActualInputProcessor() : null) :
                                pseudoGameState.peek().getActualInputProcessor()
                );
                Main.getInstance().setBackgroundColor(
                        pseudoGameState.empty() ?
                                mainGameState.getBackgroundColor() :
                                pseudoGameState.peek().getBackgroundColor()
                );
                return;
            }
        }

        gameState.create();
        Gdx.input.setInputProcessor(gameState.shouldRegisterInput() ? gameState.getActualInputProcessor() : null);

        if(gameState instanceof PseudoGameState) {
            pseudoGameState.push((PseudoGameState) gameState);
        } else {
            while(!pseudoGameState.empty())
                pseudoGameState.pop().dispose();
            if(mainGameState != null)
                mainGameState.dispose();
            mainGameState = gameState;
        }

        Main.getInstance().setBackgroundColor(gameState.getBackgroundColor());
    }

    @Override
    public void update(float deltaTime) {
        if(!pseudoGameState.empty())
            pseudoGameState.peek().update(deltaTime);
        else
            mainGameState.update(deltaTime);
    }

    @Override
    public void drawGame(SpriteBatch batch) {
        if(!pseudoGameState.empty())
            pseudoGameState.peek().drawGame(batch);
        else
            mainGameState.drawGame(batch);
    }

    @Override
    public void drawHUD(SpriteBatch batch) {
        if(!pseudoGameState.empty())
            pseudoGameState.peek().drawHUD(batch);
        else
            mainGameState.drawHUD(batch);
    }

    @Override
    public void dispose() {
        if(!pseudoGameState.empty()) pseudoGameState.pop().dispose();
        mainGameState.dispose();
    }
}
