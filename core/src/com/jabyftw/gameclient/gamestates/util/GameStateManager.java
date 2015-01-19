package com.jabyftw.gameclient.gamestates.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import com.jabyftw.gameclient.Main;
import com.jabyftw.gameclient.util.Drawable;
import com.jabyftw.gameclient.util.Tickable;

import java.util.Stack;

/**
 * Created by Isa on 01/01/2015.
 */
public class GameStateManager implements Tickable, Drawable, Disposable {

    private GameState mainGameState;
    private final Stack<PseudoGameState> pseudoGameState = new Stack<PseudoGameState>();

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
    public void draw(SpriteBatch batch) {
        if(!pseudoGameState.empty())
            pseudoGameState.peek().draw(batch);
        else
            mainGameState.draw(batch);
    }

    @Override
    public void dispose() {
        if(!pseudoGameState.empty()) pseudoGameState.pop().dispose();
        mainGameState.dispose();
    }

    public GameState getMainGameState() {
        return mainGameState;
    }

    public PseudoGameState getPseudoGameState() {
        return !pseudoGameState.empty() ? pseudoGameState.peek() : null;
    }

    public GameState getCurrentGameState() {
        return !pseudoGameState.empty() ? pseudoGameState.peek() : mainGameState;
    }
}
