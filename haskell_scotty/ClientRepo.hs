{-# LANGUAGE OverloadedStrings #-}

module ClientRepo (getClients, saveClient) where

import Client
import ConstructResult
import DBConnection
import Database.MySQL.Simple
import Database.MySQL.Simple.QueryResults
import Database.MySQL.Simple.QueryParams
import Database.MySQL.Simple.Param

getClients :: IO [Client]
getClients = do
  conn <- connectDB
  clients <- query_ conn "SELECT * FROM clients"
  return clients

instance QueryResults Client where
  convertResults fs vs = Client $... zip fs vs

saveClient :: Client -> IO Client
saveClient client = do
  conn <- connectDB
  execute conn "INSERT INTO clients \
    \(name,address,city,state,zip,country,email,contact,phone,created_at,updated_at) values \
    \(?,?,?,?,?,?,?,?,?,?,?)" client
  return client { Client.id = (1 :: Int) }

instance QueryResults Client where
  convertResults fs vs =
      Client $... constructorWrap (undefined :: Client) fs vs

instance QueryParams Client where
  renderParams
    (Client _ name address city state zip country email contact phone created_at updated_at) =
      [render name, render address, render city, render state, render zip, render country, render email, render contact, render phone, render created_at, render updated_at]
