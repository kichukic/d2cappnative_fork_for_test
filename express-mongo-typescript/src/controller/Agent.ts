import { NextFunction, Request, Response } from 'express';
import mongoose from 'mongoose';
import Agent from '../models/Agent';

const createAgent = (req: Request, res: Response, next: NextFunction) => {
    const { agentname, agentmobile, workpincode, agentemail, password } = req.body;

    const agentdetails = new Agent({
        _id: new mongoose.Types.ObjectId(),
        agentname,
        agentmobile,
        workpincode,
        agentemail,
        password
    });

    return agentdetails
        .save()
        .then((agent) => res.status(201).json({ agentdetails }))
        .catch((error) => res.status(500).json({ error }));
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

// const deleteAgent = (req: Request, res: Response, next: NextFunction) => {};

export default { createAgent, getAgentAll, getAgent, updateAgent, deleteAgent};