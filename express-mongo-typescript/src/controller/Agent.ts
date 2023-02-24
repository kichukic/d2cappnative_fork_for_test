import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import Logging from '../library/Logging';
import Agent from '../models/Agent';
import bcryptjs, { hash } from 'bcryptjs';
import signJWT from '../functions/signJTW';


const createAgent = (req: Request, res: Response, next: NextFunction) => {
    const { agent_name, agent_mobile, workpincode, agent_email, password } = req.body;

    bcryptjs.hash(password,10,(hashError,hash) => {
        if (hashError) {

            return res.status(401).json({
                message: hashError.message,
                error: hashError
            });
        }

        const agentdetails = new Agent({
            agent_id: new mongoose.Types.ObjectId(),
            agent_name,
            agent_mobile,
            workpincode,
            agent_email,
            password: hash
        });

        return agentdetails
            .save()
            .then((agent) => res.status(201).json({ agent })) //agentdetails
            .catch((error) => res.status(500).json({ error }));

    })

   
};

const getAgent = (req: Request, res: Response, next: NextFunction) => {
    const agentId =req.params.agentId

    return Agent.findById(agentId)
    .then( agent => agent ? res.status(200).json({ agent }) : res.status(404).json({ message : 'Not Found here'}) )
    .catch(error => res.status(500).json({ error }));

};

const getAgentAll = (req: Request, res: Response, next: NextFunction) => {
    return Agent.find()
        .then((agents) => res.status(200).json({ agents }))
        .catch((error) => res.status(500).json({ error }));
};


const updateAgent = (req: Request, res: Response, next: NextFunction) => {
    const agentId = req.params.agentId;

    return Agent.findById(agentId)
        .then((agent) => {
            if (agent) {
                agent.set(req.body);

                return agent
                    .save()
                    .then((agent) => res.status(201).json({ agent }))
                    .catch((error) => res.status(500).json({ error }));
            } else {
                return res.status(404).json({ message: 'not found 2 here' });
            }
        })
        .catch((error) => res.status(500).json({ error }));
};

const deleteAgent = (req: Request, res: Response, next: NextFunction) => {
    const agentId = req.params.agentId;

    return Agent.findByIdAndDelete(agentId)
        .then((agent) => (agent ? res.status(201).json({ agent, message: 'Deleted' }) : res.status(404).json({ message: 'not found 3 here' })))
        .catch((error) => res.status(500).json({ error }));
};


const validateToken = (req: Request, res: Response, next: NextFunction) => {

    Logging.info("Token validated, agent authorized")

        return res.status(200).json({
            message: 'Success Token'
        });
};
// const agentLogin =  (req: Request, res: Response, next: NextFunction) => {
//     let { agentemail, password } = req.body;

//      Agent.findOne({ agentemail })
//         .exec()
//         .then((agents: any) => {

//             // console.log("here is the console"+agents)
//             if (agents){
//                  bcryptjs.compare(password, agents.password).then((same)=>{
//                     if(same){
//                          signJWT(agents, (_error, token) => {
//                              if (_error) {
//                                  return res.status(500).json({
//                                      message: _error.message,
//                                      error: _error
//                                  });
//                              } else if (token) {
//                                  return res.status(200).json({
//                                      message: 'Auth successful',
//                                      token: token,
//                                      agent: agents
//                                  });
//                              }

//                              const _agent = new Agent({
//                                  _id: new mongoose.Types.ObjectId(),
//                                  agentemail,
//                                  password: hash
//                              });
//                          }); 
//                     }
//                  })
//             }
//             // if (agents.length !== 1) {
//             //     return res.status(401).json({
//             //         message: 'Unauthorized'
//             //     });
//             // }

//             // bcryptjs.compare(password, agents[0].password, (error, result) => {
//             //     if (error) {
//             //         return res.status(401).json({
//             //             message: 'Password Mismatch'
//             //         });
//             //     } else if (result) {
//                     // signJWT(agents[0], (_error, token) => {
//                     //     if (_error) {
//                     //         return res.status(500).json({
//                     //             message: _error.message,
//                     //             error: _error
//                     //         });
//                     //     } else if (token) {
//                     //         return res.status(200).json({
//                     //             message: 'Auth successful',
//                     //             token: token,
//                     //             agent: agents[0]
//                     //         });
//                     //     }

//                     //     const _agent = new Agent({
//                     //         _id: new mongoose.Types.ObjectId(),
//                     //         agentemail,
//                     //         password:hash
//                     //     })
//                     // }); 
//             //     }
//             // });
//         })
//         .catch((err) => {
//             console.log(err);
//             res.status(500).json({
//                 error: err
//             });
//         });
// };

const agentLogin = (req: Request, res: Response, next: NextFunction) => {
    let { agentemail, password } = req.body;

    Agent.find({ agentemail })
        .exec()
        .then((agents :any) => {
            if (agents.length !== 1) {
                return res.status(401).json({
                    message: 'Unauthorized'
                });
            }

            bcryptjs.compare(password, agents[0].password, (error, result) => {
                if (error) {
                    return res.status(401).json({
                        message: 'Password Mismatch'
                    });
                } else if (result) {
                    signJWT(agents[0], (_error, token) => {
                        if (_error) {
                            return res.status(500).json({
                                message: _error.message,
                                error: _error
                            });
                        } else if (token) {
                            return res.status(200).json({
                                message: 'Auth successful',
                                token: token,
                                agents: agents[0]
                            });
                        }
                    });
                }
            });
        })
        .catch((err) => {
            console.log(err);
            res.status(500).json({
                error: err
            });
        });
};

// const deleteAgent = (req: Request, res: Response, next: NextFunction) => {};

export default { createAgent, getAgentAll, getAgent, updateAgent, deleteAgent, validateToken, agentLogin};