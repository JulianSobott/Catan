package data;

import java.util.Random;

import org.jsfml.graphics.Color;

public abstract class DevelopmentCard {
	
	public enum Type{
		KNIGHT(new DevelopmentCard.KnightCard()),
		POINT(new DevelopmentCard.Point()),
		FREE_RESOURCES(new DevelopmentCard.FreeResources()),
		FREE_STREETS(new DevelopmentCard.FreeStreets()),
		MONOPOL(new DevelopmentCard.Monopol());
		
		private DevelopmentCard instance;
		private Type(DevelopmentCard card) {
			this.instance = card;
			this.instance.setType(this);
		}

		public DevelopmentCard getInstance() {
			return this.instance;
		}
	}

	public static class KnightCard extends DevelopmentCard{
		@Override
		public void playCard() {
			System.out.println("Knight card played");
		}
		
	}
	public static  class Point extends DevelopmentCard{
		@Override
		public void playCard() {
			System.out.println(Math.random());
		}

	}
	public static  class FreeResources extends DevelopmentCard{
		@Override
		public void playCard() {
			System.out.println("free resources card played");
		}

	}
	public static  class FreeStreets extends DevelopmentCard{
		@Override
		public void playCard() {
			System.out.println("FreeStreets card played");
		}

	}
	public static  class Monopol extends DevelopmentCard{
		@Override
		public void playCard() {
			System.out.println("Monopol card played");
		}

	}
	Type type;
		
	public abstract void playCard();
	
	public static DevelopmentCard getRandomCard() {
		Random rand = new Random();
		Type type = Type.values()[rand.nextInt(Type.values().length - 1)];
		return type.getInstance();
	}
	public void setType(Type type) {
		this.type = type;
	}
	public Type getType() {
		return this.type;
	}
	
	public Color getColor() {
		return Color.BLUE;
	}
}
