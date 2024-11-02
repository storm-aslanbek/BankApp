# main.py
from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel

import database
from database import insert_item, find_item
from bson.objectid import ObjectId

app = FastAPI()


class Item(BaseModel):
    last_name: str
    first_name: str
    phone_number: str
    password: str
    balance: int

class AuthData(BaseModel):
    phone_number: str
    password: str

class UserSearchData(BaseModel):
    phone_number: str



@app.get("/")
def read_root():
    return {"Hello": "World"}

@app.post("/register/")
async def create_item(phone: Item):
    phone_number = insert_item(phone.dict())
    return {"id": str(phone_number)}

@app.post("/auth/")
async def login(login_data: AuthData):
    user = database.collection.find_one({"phone_number": login_data.phone_number})

    if user and user["password"] == login_data.password:
        return {"last_name": f"{user['last_name']}", "first_name": f"{user['first_name']}", "phone_number": f"{user['phone_number']}",
                "password": f"{user['password']}", "balance": f"{user['balance']}"}
    else:
        raise HTTPException(status_code=401, detail="Invalid phone number or password")

@app.post("/user_search/")
async def user_search(user_search_data: UserSearchData):
    user = database.collection.find_one({"phone_number": user_search_data.phone_number})

    if user:
        return {"last_name": f"{user['last_name']}", "first_name": f"{user['first_name']}", "phone_number": f"{user['phone_number']}",
                "password": f"{user['password']}", "balance": f"{user['balance']}"}
    else:
        raise HTTPException(status_code=401, detail="Invalid phone number or password")




@app.get("/auth/{phone_number}")
async def read_item(phone_number: str):
    item = find_item(phone_number)
    if item is None:
        raise HTTPException(status_code=404, detail="Item not found")
    item["id"] = str(item["_id"])
    del item["_id"]
    return item

