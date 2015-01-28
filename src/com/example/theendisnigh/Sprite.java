package com.example.theendisnigh;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Sprite {
	
	private int x, y;
	private int spriteHeight, spriteWidth;
	private Bitmap b;
	private int rowCounter = 0;
	private int columnCounter = 0;
	private int delayCounter = 0;
	private Rect sourceRect;
	private Rect dst;
	private int row;
	private int column;
	private double delay;
	private double fps;
	private double overallDelay;
	
	public Sprite (Bitmap b, int x, int y, int c, int r, int f, double d){
		this.b = b;
		this.row = r;
		this.column = c;
		this.fps = f;
		this.delay = d;
		this.spriteHeight = b.getHeight() / this.row;  //Get the height of individual sprites
		this.spriteWidth = b.getWidth() / this.column; //Get the width of individual sprites
		this.x = x; //Position of sprite
		this.y = y;
		
		//Calculate the delay based on the frames per second and milliseconds
		overallDelay = ((this.delay / 1000) * fps);

		//Starting source rectangle placed on the spritesheet 
		sourceRect = new Rect (0,0, spriteWidth, spriteHeight);
	}
	
	public void update(){
		
		//Count the number of frames during the delay process
		delayCounter++;
		
		if ((int)(overallDelay)==delayCounter){
			if (columnCounter >= this.column){
				columnCounter = 0;
			}
			
			if (rowCounter >= this.row){
				rowCounter = 0;
			}
			
			//Position of rectangle on spritesheet
			this.sourceRect.left = columnCounter * spriteWidth;
			this.sourceRect.right = this.sourceRect.left + spriteWidth;
			this.sourceRect.top =  rowCounter * spriteHeight;
			this.sourceRect.bottom = this.sourceRect.top + spriteHeight ;
				
			//Counters to iterate over spritesheet
			columnCounter++;
			rowCounter++;
			
			//Reset the counter to zero
			delayCounter = 0;
		}
	}
	
	@SuppressLint("DrawAllocation")
	public void draw(Canvas canvas){
		update();
		
		//Rectangle that defines where sprite appears on canvas
		dst = new Rect(this.x,this.y,this.x+spriteWidth, this.y+ spriteHeight); //Scales the picture
		
		//Output of bitmap
		canvas.drawBitmap(b, sourceRect, dst, null);
	}
	
	//Get rectangle position of sprite on canvas
	public Rect getDest(){
		return this.dst;
	}
	
	//set x position
	public void setX(int x){
		this.x = x;
	}
	
	//set y position
		public void setY(int y){
			this.y = y;
		}

}