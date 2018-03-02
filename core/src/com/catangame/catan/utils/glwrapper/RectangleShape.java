package com.catangame.catan.utils.glwrapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.catangame.catan.local.Framework;
import com.catangame.catan.utils.Color;
import org.jsfml.system.Vector2f;

public class RectangleShape extends Renderable {
    private org.jsfml.graphics.RectangleShape sfml_rs;

    private Rectangle bounds;
    private Color backColor;
    private Color outlineColor;
    private float outlineThickness = 2;

    public RectangleShape() {
    }

    public RectangleShape(org.jsfml.graphics.RectangleShape sfml_rs) {
        this.sfml_rs = sfml_rs;
    }

    public void draw(RenderTarget rt) {
        if (Framework.usingGdx) {
            rt.sr.begin(ShapeType.Filled);
            rt.sr.setColor(backColor.gdx());
            rt.sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            rt.sr.end();

            if (outlineThickness > 0) {
                rt.sr.begin(ShapeType.Line);
                rt.sr.setColor(outlineColor.gdx());
                Gdx.gl.glLineWidth(outlineThickness);
                rt.sr.rect(bounds.x, bounds.y, bounds.width, bounds.height);
                rt.sr.end();
            }
        } else {
            rt.rt.draw(sfml_rs);
        }
    }

    public void setRect(Rectangle rect) {
        if (Framework.usingGdx) {
            bounds = rect;
        } else {
            sfml_rs.setPosition(rect.x, rect.y);
            sfml_rs.setSize(new Vector2f(rect.width, rect.height));
        }

    }

    public void setOutline(Color color, float thickness) {
        if (Framework.usingGdx) {
            outlineColor = color;
            outlineThickness = thickness;
        } else {
            sfml_rs.setOutlineColor(color.sfml());
            sfml_rs.setOutlineThickness(thickness);
        }
    }

    public void setFillColor(Color color) {
        if (Framework.usingGdx) {
            backColor = color;
        } else
            sfml_rs.setFillColor(color.sfml());
    }
}
